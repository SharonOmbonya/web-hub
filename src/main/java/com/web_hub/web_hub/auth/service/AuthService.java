package com.web_hub.web_hub.auth.service;


import com.web_hub.web_hub.admin.InviteUserRequest;
import com.web_hub.web_hub.admin.UpdateUserRequest;
import com.web_hub.web_hub.admin.UserResponse;
import com.web_hub.web_hub.auth.api.dto.ForgotPasswordRequest;
import com.web_hub.web_hub.auth.api.dto.ResendOtpRequest;
import com.web_hub.web_hub.auth.api.dto.ResetPasswordRequest;
import com.web_hub.web_hub.auth.api.dto.VerifyResetOtpRequest;
import com.web_hub.web_hub.dto.*;
import com.web_hub.web_hub.emailService.EmailService;
import com.web_hub.web_hub.exception.AuthException;
import com.web_hub.web_hub.exception.ResourceNotFoundException;
import com.web_hub.web_hub.hr.Employees.model.Employee;
import com.web_hub.web_hub.hr.Employees.repository.EmployeeRepository;
import com.web_hub.web_hub.jwt.JwtService;
import com.web_hub.web_hub.role.Role;
import com.web_hub.web_hub.user.model.User;
import com.web_hub.web_hub.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final EmployeeRepository employeeRepository;

    /* =========================================================
       REGISTER USER
       ========================================================= */
    public void register(@Valid RegisterRequest request) {
        if (userRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new AuthException("User already exists");
        }

        User user = User.builder()
                .username(request.email())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ADMIN)
                .active(true)
                .build();

        userRepository.save(user);
    }

    /* =========================================================
       SELF REGISTRATION
       ========================================================= */
    public void selfRegister(@Valid SelfRegisterRequest request) {
        if (userRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new AuthException("User already exists");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .active(true)
                .build();

        userRepository.save(user);
    }

    /* =========================================================
       INVITE USER (ADMIN ONLY)
       ========================================================= */
    public UserResponse inviteUser(InviteUserRequest request) {
        // 1. Locate the pre-existing Employee record using the request email
        Employee employee = employeeRepository.findByEmail(request.email().trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No employee profile found with email: " + request.email() +
                                ". Create the employee profile before provisioning a user account."
                ));

        // 2. Prevent duplicate user accounts
        userRepository.findByEmailIgnoreCase(request.email().trim())
                .ifPresent(u -> {
                    throw new AuthException("User already exists with this email address");
                });

        String inviteToken = UUID.randomUUID().toString();

        // 3. Build the User entity, attaching the discovered Employee
        User user = User.builder()
                .username(request.email().toLowerCase().trim())
                .email(request.email().trim())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .employee(employee) // Connects the OneToOne mapping
                .role(Role.valueOf(request.role().toUpperCase()))
                .active(false)
                .inviteToken(inviteToken)
                .inviteExpiry(Instant.now().plus(2, java.time.temporal.ChronoUnit.DAYS))
                .forcePasswordChange(true)
                .build();

        User savedUser = userRepository.save(user);

        // 4. Dispatch the onboarding invitation link
        String link = "http://localhost:3000/auth/set-password?token=" + inviteToken;
        emailService.sendOnboardingInvite(savedUser.getEmail(), link);

        // 5. Parse Employee join date safely for the response object
        java.time.LocalDate joinDate = null;
        if (employee.getStartDate() != null) {
            try {
                joinDate = java.time.LocalDate.parse(employee.getStartDate());
            } catch (Exception e) {
                // Fallback if format differs from ISO standard
            }
        }

        // 6. Return response pulling master profile text directly from the Employee source of truth
        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getUsername(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getJobTitle(),
                employee.getPhone(),
                employee.getDepartment(),
                joinDate,
                savedUser.getRole(),
                savedUser.isActive(),
                inviteToken
        );
    }

    /* =========================================================
       COMPLETE REGISTRATION (INVITE TOKEN)
       ========================================================= */
    public AuthResponse completeRegistration(String token, String password) {
        User user = userRepository.findByInviteToken(token)
                .orElseThrow(() -> new AuthException("Invalid or expired invite"));

        if (user.getInviteExpiry() == null || user.getInviteExpiry().isBefore(Instant.now())) {
            throw new AuthException("Invite expired");
        }

        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordSet(true);
        user.setActive(true);
        user.setApproved(true);
        user.setInviteToken(null);
        user.setInviteExpiry(null);

        userRepository.save(user);

        return issueTokens(user);
    }

    /* =========================================================
       LOGIN → SEND OTP
       ========================================================= */
    public AuthResponse authenticate(@Valid AuthRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!user.isActive()) throw new AuthException("Account disabled");

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new BadCredentialsException("Invalid credentials");

        String otp = generateOtp();
        user.setMfaOtp(otp);
        user.setMfaOtpExpiry(Instant.now().plus(5, ChronoUnit.MINUTES));
        userRepository.save(user);

        emailService.sendLoginOtp(user.getEmail(), otp);

        return AuthResponse.mfaRequired();
    }

    /* =========================================================
       VERIFY OTP → ISSUE TOKENS
       ========================================================= */
    public AuthResponse verifyMfa(@Valid VerifyMfaRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new AuthException("User not found"));

        if (user.getMfaOtp() == null) throw new AuthException("OTP not generated");
        if (user.getMfaOtpExpiry().isBefore(Instant.now())) throw new AuthException("OTP expired");
        if (!user.getMfaOtp().equals(request.otp())) throw new AuthException("Invalid OTP");

        user.setMfaOtp(null);
        user.setMfaOtpExpiry(null);
        userRepository.save(user);

        return issueTokens(user);
    }

    /* =========================================================
       STEP 1: FORGOT PASSWORD (SEND OTP)
       ========================================================= */
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new AuthException("User not found"));

        String otp = generateOtp();
        user.setResetOtp(otp);
        user.setResetOtpExpiry(Instant.now().plus(2, ChronoUnit.MINUTES));
        userRepository.save(user);

        emailService.sendPasswordResetOtp(user.getEmail(), otp);
    }

    /* =========================================================
       STEP 2: VERIFY OTP (ISSUE RESET TOKEN)
       ========================================================= */
    public String verifyResetOtp(VerifyResetOtpRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new AuthException("User not found"));

        if (user.getResetOtp() == null || !user.getResetOtp().equals(request.otp())) {
            throw new AuthException("Invalid OTP");
        }

        if (user.getResetOtpExpiry().isBefore(Instant.now())) {
            throw new AuthException("OTP has expired");
        }

        user.setResetOtp(null);
        user.setResetOtpExpiry(null);

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(Instant.now().plus(15, ChronoUnit.MINUTES));
        userRepository.save(user);

        return resetToken;
    }

    /* =========================================================
       STEP 3: RESET PASSWORD WITH TOKEN
       ========================================================= */
    public void resetPasswordWithToken(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.token())
                .orElseThrow(() -> new AuthException("Invalid or expired reset token"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(Instant.now())) {
            throw new AuthException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        emailService.sendPasswordResetConfirmation(user.getEmail());
    }

    /* =========================================================
       REFRESH TOKEN
       ========================================================= */
    public AuthResponse refreshToken(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new AuthException("Invalid refresh token"));

        if (user.getRefreshTokenExpiry().isBefore(Instant.now()))
            throw new AuthException("Refresh token expired");

        return issueTokens(user);
    }

    /* =========================================================
       GET USERS
       ========================================================= */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 👇 ADDED BACK: Get User By ID Logic
    /* =========================================================
       GET USER BY ID
       ========================================================= */
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found"));

        return mapToResponse(user);
    }

    /* =========================================================
       UPDATE USER
       ========================================================= */
    @Transactional // Ensuring both User and Employee updates commit atomically
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found"));

        // 1. Update core system/account fields
        if (request.email() != null) user.setEmail(request.email());
        if (request.username() != null) user.setUsername(request.username());
        if (request.role() != null) user.setRole(request.role());
        if (request.active() != null) user.setActive(request.active());

        // 2. Route master profile updates directly to the linked Employee record
        Employee employee = user.getEmployee();
        if (employee != null) {
            if (request.firstName() != null) employee.setFirstName(request.firstName());
            if (request.lastName() != null) employee.setLastName(request.lastName());
            if (request.jobTitle() != null) employee.setJobTitle(request.jobTitle());
            if (request.phoneNumber() != null) employee.setPhone(request.phoneNumber());
            if (request.department() != null) employee.setDepartment(request.department());

            // Keep emails identical across account and master profile if changed
            if (request.email() != null) employee.setEmail(request.email());
        }

        userRepository.save(user);

        return mapToResponse(user);
    }

    /* =========================================================
       SUSPEND USER
       ========================================================= */
    public void suspendUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found"));

        user.setActive(false);
        user.setLastLogoutDate(Instant.now());
        userRepository.save(user);
    }

    /* =========================================================
       ADMIN HARD-RESET PASSWORD
       ========================================================= */
    public void resetPassword(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found"));

        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);
    }

    public void resendOtp(ResendOtpRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new AuthException("User not found"));

        if (!user.isActive()) {
            throw new AuthException("Account disabled");
        }

        String otp = generateOtp();

        user.setMfaOtp(otp);
        user.setMfaOtpExpiry(Instant.now().plus(2, ChronoUnit.MINUTES));

        userRepository.save(user);

        emailService.sendLoginOtp(user.getEmail(), otp);
    }

    /* =========================================================
       MAPPER
       ========================================================= */
    private UserResponse mapToResponse(User user) {
        Employee employee = user.getEmployee();

        // Safely pull up-to-date master profile details directly from Employee
        String firstName = (employee != null) ? employee.getFirstName() : null;
        String lastName = (employee != null) ? employee.getLastName() : null;
        String jobTitle = (employee != null) ? employee.getJobTitle() : null;
        String phoneNumber = (employee != null) ? employee.getPhone() : null;
        String department = (employee != null) ? employee.getDepartment() : null;

        // Parse Employee startDate into LocalDate safely for the DTO response
        java.time.LocalDate joinDate = null;
        if (employee != null && employee.getStartDate() != null) {
            try {
                joinDate = java.time.LocalDate.parse(employee.getStartDate());
            } catch (Exception e) {
                // Fallback if string date format varies from ISO standard standard format
            }
        }

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                firstName,
                lastName,
                jobTitle,
                phoneNumber,
                department,
                joinDate,
                user.getRole(),
                user.isActive(),
                null // Keep token null for general fetching
        );
    }

    /* =========================================================
       LOGOUT
       ========================================================= */
    public void logout(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new AuthException("Invalid refresh token"));

        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        user.setLastLogoutDate(Instant.now());
        userRepository.save(user);
    }

    /* =========================================================
       ISSUE TOKENS
       ========================================================= */
    private AuthResponse issueTokens(User user) {
        String refresh = UUID.randomUUID().toString();

        user.setRefreshToken(refresh);
        user.setRefreshTokenExpiry(Instant.now().plus(30, ChronoUnit.DAYS));
        userRepository.save(user);

        String access = jwtService.generateToken(
                user,
                Map.of(
                        "userId", user.getId(),
                        "role", user.getRole().name()
                )
        );

        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .firstLogin(user.isForcePasswordChange())
                .build();
    }

    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
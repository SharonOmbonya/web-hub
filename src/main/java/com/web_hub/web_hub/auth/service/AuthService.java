package com.web_hub.web_hub.auth.service;

import com.web_hub.web_hub.admin.CreateUserRequest;
import com.web_hub.web_hub.admin.UpdateUserRequest;
import com.web_hub.web_hub.admin.UserResponse;
import com.web_hub.web_hub.auth.api.dto.ForgotPasswordRequest;
import com.web_hub.web_hub.auth.api.dto.ResetPasswordRequest;
import com.web_hub.web_hub.auth.api.dto.VerifyResetOtpRequest;
import com.web_hub.web_hub.dto.*;
import com.web_hub.web_hub.emailService.EmailService;
import com.web_hub.web_hub.exception.AuthException;
import com.web_hub.web_hub.jwt.JwtService;
import com.web_hub.web_hub.role.Role;
import com.web_hub.web_hub.user.model.User;
import com.web_hub.web_hub.user.repository.UserRepository;
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
    public UserResponse createUser(CreateUserRequest request) {
        userRepository.findByEmailIgnoreCase(request.email())
                .ifPresent(u -> {
                    throw new AuthException("User already exists"); // Updated from RuntimeException
                });

        String inviteToken = UUID.randomUUID().toString();

        User user = User.builder()
                .username(request.email())
                .email(request.email().trim())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .jobTitle(request.jobTitle())
                .phoneNumber(request.phoneNumber())
                .department(request.department())
                .location(request.location())
                .joinDate(java.time.LocalDate.now())
                .role(Role.valueOf(request.role().toUpperCase()))
                .active(false)
                .inviteToken(inviteToken)
                .inviteExpiry(Instant.now().plus(1, ChronoUnit.DAYS))
                .forcePasswordChange(true)
                .build();

        userRepository.save(user);

        String link = "http://localhost:3000/setup-password?token=" + inviteToken;
        emailService.sendOnboardingInvite(user.getEmail(), link);

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getJobTitle(),
                user.getPhoneNumber(),
                user.getDepartment(),
                user.getLocation(),
                user.getJoinDate(),
                user.getRole(),
                user.isActive(),
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
        // Changed to IgnoreCase to prevent case-sensitivity login issues
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
        user.setResetOtpExpiry(Instant.now().plus(15, ChronoUnit.MINUTES));
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

    /* =========================================================
       UPDATE USER
       ========================================================= */
    /* =========================================================
       UPDATE USER
       ========================================================= */
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found"));

        // Existing fields
        if (request.email() != null) user.setEmail(request.email());
        if (request.username() != null) user.setUsername(request.username());
        if (request.role() != null) user.setRole(request.role());
        if (request.active() != null) user.setActive(request.active());

        // --- NEW PROFILE FIELDS ---
        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.jobTitle() != null) user.setJobTitle(request.jobTitle());
        if (request.phoneNumber() != null) user.setPhoneNumber(request.phoneNumber());
        if (request.department() != null) user.setDepartment(request.department());
        if (request.location() != null) user.setLocation(request.location());
        // --------------------------

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

    /* =========================================================
       MAPPER
       ========================================================= */
    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getFirstName(),   // Added
                user.getLastName(),    // Added
                user.getJobTitle(),    // Added
                user.getPhoneNumber(), // Added
                user.getDepartment(),  // Added
                user.getLocation(),    // Added
                user.getJoinDate(),    // Added
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
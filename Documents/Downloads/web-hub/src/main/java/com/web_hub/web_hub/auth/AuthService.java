package com.web_hub.web_hub.auth;


import com.web_hub.web_hub.admin.dto.*;
import com.web_hub.web_hub.dto.*;
import com.web_hub.web_hub.emailService.EmailService;
import com.web_hub.web_hub.exception.AuthException;
import com.web_hub.web_hub.jwt.JwtService;


import com.web_hub.web_hub.role.Role;
import com.web_hub.web_hub.user.User;
import com.web_hub.web_hub.user.UserRepository;
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

        if (userRepository.findByEmail(request.email()).isPresent()) {
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

        if (userRepository.findByEmail(request.email()).isPresent()) {
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
    public UserResponse createUser(CreateUserRequest request) {

        userRepository.findByEmailIgnoreCase(request.email())
                .ifPresent(u -> {
                    throw new RuntimeException("User already exists");
                });

        User user = User.builder()
                .username(request.email()) //
                .email(request.email().trim())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.valueOf(request.role().toUpperCase())) //
                .active(true)
                .forcePasswordChange(true)
                .build();

        userRepository.save(user);

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                user.isActive()
        );
    }
    /* =========================================================
       LOGIN → SEND OTP
       ========================================================= */
    public AuthResponse authenticate(@Valid AuthRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!user.isActive())
            throw new AuthException("Account disabled");

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

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthException("User not found"));

        if (user.getMfaOtp() == null)
            throw new AuthException("OTP not generated");

        if (user.getMfaOtpExpiry().isBefore(Instant.now()))
            throw new AuthException("OTP expired");

        if (!user.getMfaOtp().equals(request.otp()))
            throw new AuthException("Invalid OTP");

        user.setMfaOtp(null);
        user.setMfaOtpExpiry(null);

        return issueTokens(user);
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
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found"));

        if (request.email() != null) {
            user.setEmail(request.email());
        }

        if (request.role() != null) {
            user.setRole(request.role());
        }

        if (request.active() != null) {
            user.setActive(request.active());
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
        userRepository.save(user);
    }

    /* =========================================================
       RESET PASSWORD
       ========================================================= */
    public void resetPassword(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found"));

        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);
    }

    /* =========================================================
       DEPARTMENTS
       ========================================================= */
    public String getDepartments() {
        return "Departments fetched";
    }

    public void createDepartment(DepartmentRequest request) {
        // you can implement DB later
    }

    /* =========================================================
       ASSETS
       ========================================================= */
    public String getAssets() {
        return "Assets fetched";
    }

    /* =========================================================
       AUDIT LOGS
       ========================================================= */
    public String getAuditLogs() {
        return "Audit logs fetched";
    }

    /* =========================================================
       ANNOUNCEMENTS
       ========================================================= */
    public void sendAnnouncement(AnnouncementRequest request) {
        // implement later
    }

    /* =========================================================
       MAPPER
       ========================================================= */
    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                user.isActive()
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
                .firstLogin(user.isForcePasswordChange()) // ✅ ADD THIS
                .build();
    }

    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}

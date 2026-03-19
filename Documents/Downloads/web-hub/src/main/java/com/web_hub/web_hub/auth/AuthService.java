package com.web_hub.web_hub.auth;


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
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
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

        return AuthResponse.success(access, refresh);
    }

    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}

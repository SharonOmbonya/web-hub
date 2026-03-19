package Meraki.Hub.Management.System.auth.service;

import Meraki.Hub.Management.System.auth.api.dto.*;
import Meraki.Hub.Management.System.exceptions.AuthException;
import Meraki.Hub.Management.System.security.JwtService;
import Meraki.Hub.Management.System.user.model.User;
import Meraki.Hub.Management.System.user.repository.UserRepository;
import Meraki.Hub.Management.System.roles.model.Role;
import Meraki.Hub.Management.System.roles.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final RoleRepository roleRepository;

    /* =========================================================
       REGISTER USER
       ========================================================= */
    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthException("User already exists");
        }

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .active(true)
                .build();

        userRepository.save(user);
    }

    /* =========================================================
       SELF REGISTRATION
       ========================================================= */
    public void selfRegister(SelfRegisterRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new AuthException("User already exists");
        }

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .active(true)
                .build();

        userRepository.save(user);
    }

    /* =========================================================
       LOGIN → SEND OTP
       ========================================================= */
    public AuthResponse authenticate(AuthRequest request) {

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

        return AuthResponse.mfaRequired();
    }

    /* =========================================================
       VERIFY OTP → ISSUE TOKENS
       ========================================================= */
    public AuthResponse verifyMfa(VerifyMfaRequest request) {

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
                (Map<String, Object>) user,
                (UserDetails) Map.of(
                        "userId", user.getId(),
                        "role", user.getRole().getName()
                )
        );

        return AuthResponse.success(access, refresh);
    }

    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    /* =========================================================
       ADMIN REGISTRATION
       ========================================================= */
    public RegisterAdminResponse registerAdmin(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Admin already exists");
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        User admin = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(adminRole)
                .active(true)
                .approved(true)
                .build();

        userRepository.save(admin);

        return new RegisterAdminResponse(
                admin.getId(),
                admin.getEmail(),
                admin.getFullName(),
                admin.getRole().getName(),
                admin.isApproved()
        );
    }

    /* =========================================================
       ADMIN LOGIN
       ========================================================= */
    public AuthResponse adminLogin(AuthDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!user.getRole().getName().equals("ADMIN")) {
            throw new RuntimeException("Access denied: Not an admin account");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!user.isActive()) {
            throw new RuntimeException("Admin account disabled");
        }

        return issueTokens(user);
    }
}

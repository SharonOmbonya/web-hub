package com.web_hub.web_hub.auth;
import com.web_hub.web_hub.dto.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /* ================= REGISTER ================= */

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        authService.register(request);
        return ResponseEntity.ok("User registered");
    }

    /* ================= LOGIN ================= */

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    /* ================= VERIFY MFA ================= */

    @PostMapping("/verify-mfa")
    public ResponseEntity<AuthResponse> verifyMfa(
            @Valid @RequestBody VerifyMfaRequest request
    ) {
        return ResponseEntity.ok(authService.verifyMfa(request));
    }

    /* ================= REFRESH TOKEN ================= */

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request.refreshToken()));
    }



    @PostMapping("/self-register")
    public ResponseEntity<String> selfRegister(
            @Valid @RequestBody SelfRegisterRequest request
    ) {
        authService.selfRegister(request);
        return ResponseEntity.ok("Registration successful");
    }

    /* ================= LOGOUT ================= */

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestBody RefreshTokenRequest request
    ) {
        authService.logout(request.refreshToken());
        return ResponseEntity.ok("Logged out");
    }
}

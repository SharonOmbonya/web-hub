package com.web_hub.web_hub.auth;
import com.web_hub.web_hub.admin.CreateUserRequest;
import com.web_hub.web_hub.admin.UpdateUserRequest;
import com.web_hub.web_hub.admin.UserResponse;
import com.web_hub.web_hub.dto.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody CreateUserRequest request
    ) {
        return ResponseEntity.ok(authService.createUser(request));
    }

    @PostMapping("/complete-registration")
    public ResponseEntity<AuthResponse> completeRegistration(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                authService.completeRegistration(
                        request.get("token"),
                        request.get("password")
                )
        );
    }
    /* ================= GET USERS ================= */

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    /* ================= UPDATE USER ================= */

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(authService.updateUser(id, request));
    }

    /* ================= SUSPEND USER ================= */

    @PatchMapping("/users/{id}/suspend")
    public ResponseEntity<String> suspendUser(@PathVariable Long id) {
        authService.suspendUser(id);
        return ResponseEntity.ok("User suspended");
    }

    /* ================= RESET PASSWORD ================= */

    @PatchMapping("/users/{id}/reset-password")
    public ResponseEntity<String> resetPassword(@PathVariable Long id) {
        authService.resetPassword(id);
        return ResponseEntity.ok("Password reset");
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

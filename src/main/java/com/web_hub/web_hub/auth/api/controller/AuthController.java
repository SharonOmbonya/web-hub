package com.web_hub.web_hub.auth.api.controller;

import com.web_hub.web_hub.admin.CreateUserRequest;
import com.web_hub.web_hub.admin.UpdateUserRequest;
import com.web_hub.web_hub.admin.UserResponse;
import com.web_hub.web_hub.auth.service.AuthService;
import com.web_hub.web_hub.dto.*;
import com.web_hub.web_hub.auth.api.dto.ForgotPasswordRequest;
import com.web_hub.web_hub.auth.api.dto.ResetPasswordRequest;
import com.web_hub.web_hub.auth.api.dto.SetupUserPasswordRequest;
import com.web_hub.web_hub.auth.api.dto.VerifyResetOtpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /* ================= PUBLIC ENDPOINTS ================= */

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(Map.of("message", "User registered"));
    }

    @PostMapping("/self-register")
    public ResponseEntity<Map<String, String>> selfRegister(@Valid @RequestBody SelfRegisterRequest request) {
        authService.selfRegister(request);
        return ResponseEntity.ok(Map.of("message", "Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/verify-mfa")
    public ResponseEntity<AuthResponse> verifyMfa(@Valid @RequestBody VerifyMfaRequest request) {
        return ResponseEntity.ok(authService.verifyMfa(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.refreshToken()));
    }

    @PostMapping("/setup-password")
    public ResponseEntity<AuthResponse> setupPassword(@Valid @RequestBody SetupUserPasswordRequest request) {
        return ResponseEntity.ok(authService.completeRegistration(request.token(), request.newPassword()));
    }

    // --- SELF-SERVICE PASSWORD RESET ENDPOINTS ---
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(Map.of("message", "If an account exists, a reset OTP has been sent."));
    }

    @PostMapping("/verify-reset-otp")
    public ResponseEntity<Map<String, String>> verifyResetOtp(@Valid @RequestBody VerifyResetOtpRequest request) {
        String resetToken = authService.verifyResetOtp(request);
        return ResponseEntity.ok(Map.of("resetToken", resetToken));
    }

    @PostMapping("/reset-password-with-token")
    public ResponseEntity<Map<String, String>> resetPasswordWithToken(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPasswordWithToken(request);
        return ResponseEntity.ok(Map.of("message", "Password has been successfully reset."));
    }

    /* ================= PROTECTED ADMIN ENDPOINTS ================= */

    @PostMapping("/invite")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity.ok(authService.createUser(request));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(authService.updateUser(id, request));
    }

    @PatchMapping("/users/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> suspendUser(@PathVariable Long id) {
        authService.suspendUser(id);
        return ResponseEntity.ok(Map.of("message", "User suspended"));
    }

    @PatchMapping("/users/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable Long id) {
        authService.resetPassword(id);
        return ResponseEntity.ok(Map.of("message", "Password reset by admin"));
    }

    /* ================= PROTECTED USER ENDPOINTS ================= */

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }
}
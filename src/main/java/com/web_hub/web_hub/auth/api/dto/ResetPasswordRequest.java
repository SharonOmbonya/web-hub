package com.web_hub.web_hub.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(@NotBlank String token, @NotBlank String newPassword) {}

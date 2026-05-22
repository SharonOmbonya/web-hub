package com.web_hub.web_hub.auth.api.dto;


import jakarta.validation.constraints.NotBlank;

public record SetupUserPasswordRequest(
        @NotBlank String token,
        @NotBlank String newPassword
) {}

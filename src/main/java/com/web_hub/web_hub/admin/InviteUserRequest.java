package com.web_hub.web_hub.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public record InviteUserRequest(
        @NotBlank @Email String email,
        @NotBlank String role
) {}
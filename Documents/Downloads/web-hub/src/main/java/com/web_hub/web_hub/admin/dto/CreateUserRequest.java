package com.web_hub.web_hub.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(

        @NotBlank
        String email,

        @NotBlank
        String password,

        @NotBlank
        String role,

        String username // ✅ added

) {}

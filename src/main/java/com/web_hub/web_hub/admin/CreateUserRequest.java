package com.web_hub.web_hub.admin;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Email;

public record CreateUserRequest(
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String jobTitle,
        @NotBlank String phoneNumber,
        @NotBlank String department,
        @NotBlank String location,
        @NotBlank String role
) {}

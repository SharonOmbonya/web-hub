package com.web_hub.web_hub.emailService;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.context.annotation.Bean;

public record VerifyEmailRequest(
@Bean
        @Email
        @NotBlank
        String email,

        @NotBlank
        String otp
) {}

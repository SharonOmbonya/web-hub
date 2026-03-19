package com.web_hub.web_hub.dto;


import jakarta.validation.constraints.NotBlank;

public record AuthRequest(

        @NotBlank
        String email,

        @NotBlank
        String password

) {}

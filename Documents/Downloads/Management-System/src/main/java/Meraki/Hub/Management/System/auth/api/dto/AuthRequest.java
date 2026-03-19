package Meraki.Hub.Management.System.auth.api.dto;


import jakarta.validation.constraints.NotBlank;

public record AuthRequest(

        @NotBlank
        String email,

        @NotBlank
        String password

) {}

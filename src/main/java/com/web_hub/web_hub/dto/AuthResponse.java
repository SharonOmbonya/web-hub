package com.web_hub.web_hub.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private boolean mfaRequired;
    private String accessToken;
    private String refreshToken;
    private boolean firstLogin;

    public static AuthResponse mfaRequired() {
        return AuthResponse.builder()
                .mfaRequired(true)
                .build();
    }

    public static AuthResponse success(String access, String refresh) {
        return AuthResponse.builder()
                .accessToken(access)
                .firstLogin(false)
                .refreshToken(refresh)
                .build();
    }
}

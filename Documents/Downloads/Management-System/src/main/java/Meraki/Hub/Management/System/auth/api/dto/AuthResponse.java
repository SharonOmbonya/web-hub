package Meraki.Hub.Management.System.auth.api.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private boolean mfaRequired;
    private String accessToken;
    private String refreshToken;

    public static AuthResponse mfaRequired() {
        return AuthResponse.builder()
                .mfaRequired(true)
                .build();
    }

    public static AuthResponse success(String access, String refresh) {
        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }
}

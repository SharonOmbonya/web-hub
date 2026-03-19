package com.web_hub.web_hub.emailService;


import java.time.Instant;

public record ApiSuccessResponse(
        int status,
        String message,
        Instant timestamp
) {
    public static ApiSuccessResponse ok(String message) {
        return new ApiSuccessResponse(200, message, Instant.now());
    }
}

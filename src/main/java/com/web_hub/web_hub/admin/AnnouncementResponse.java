package com.web_hub.web_hub.admin;

import java.time.LocalDateTime;

public record AnnouncementResponse(
        Long id,
        String title,
        String message,
        String createdBy,
        LocalDateTime createdAt
) {}

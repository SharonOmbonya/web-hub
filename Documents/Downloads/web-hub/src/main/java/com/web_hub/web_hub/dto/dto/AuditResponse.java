package com.web_hub.web_hub.dto.dto;

public record AuditResponse(
        Long id,
        String action,
        String performedBy,
        String timestamp
) {}

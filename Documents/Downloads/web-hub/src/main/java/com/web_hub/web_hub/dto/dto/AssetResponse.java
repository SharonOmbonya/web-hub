package com.web_hub.web_hub.dto.dto;

public record AssetResponse(
        Long id,
        String name,
        String type,
        Long assignedUserId
) {}

package com.web_hub.web_hub.dto.dto;

public record AssetRequest(
        String name,
        String type,
        Long assignedUserId
) {}

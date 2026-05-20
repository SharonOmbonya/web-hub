package com.web_hub.web_hub.admin;

public record AssignRoleRequest(
        Long userId,
        String role
) {}

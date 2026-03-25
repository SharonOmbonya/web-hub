package com.web_hub.web_hub.dto.dto;

import com.web_hub.web_hub.role.Role;

public record UserResponse(
        Long id,
        String email,
        String username,
        Role role,
        boolean active
) {}

package com.web_hub.web_hub.dto.dto;

import com.web_hub.web_hub.role.Role;

public record UpdateUserRequest(
        String username,
        String email,
        Role role,
        Boolean active
) {}

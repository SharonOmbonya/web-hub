package com.web_hub.web_hub.dto.dto;

import com.web_hub.web_hub.role.Role;

public record CreateUserRequest(
        String email,
        String password,
        String username,
        Role role
) {}

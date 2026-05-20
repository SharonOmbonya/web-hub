package com.web_hub.web_hub.user;

import com.web_hub.web_hub.role.Role;

public record UserProfileResponse(
        Long id,
        String email,
        String username,
        Role role,
        boolean active
) {}

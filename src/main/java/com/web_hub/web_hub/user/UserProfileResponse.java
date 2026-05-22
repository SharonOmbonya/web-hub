package com.web_hub.web_hub.user;

import com.web_hub.web_hub.role.Role;
import java.time.LocalDate;

public record UserProfileResponse(
        Long id,
        String email,
        String username,
        String firstName,
        String lastName,
        String jobTitle,
        String phoneNumber,
        String department,
        String location,
        LocalDate joinDate,
        Role role,
        boolean active
) {}
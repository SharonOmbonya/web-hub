package com.web_hub.web_hub.user;

import com.web_hub.web_hub.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    public UserProfileResponse getMyProfile() {
        User user = getCurrentUser();

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getFirstName(),  // Added
                user.getLastName(),   // Added
                user.getJobTitle(),   // Added
                user.getPhoneNumber(),// Added
                user.getDepartment(), // Added
                user.getLocation(),   // Added
                user.getJoinDate(),   // Added
                user.getRole(),
                user.isActive()
        );
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthException("User not found"));
    }
}
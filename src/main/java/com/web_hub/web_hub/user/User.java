package com.web_hub.web_hub.user;


import com.web_hub.web_hub.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean active;

    // MFA
    private String mfaOtp;
    private Instant mfaOtpExpiry;
    private boolean approved;
    private boolean emailVerified;
    private boolean passwordSet;
    private boolean forcePasswordChange;
    private int failedLoginAttempts;
    private Instant lockedUntil;

    private boolean onboardingCompleted;

    private Instant createdAt;
    private Instant updatedAt;
    private String inviteToken;
    private Instant inviteExpiry;
    // Refresh tokens
    private String refreshToken;
    private Instant refreshTokenExpiry;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + role.name());
    }

    // 👇 Decide what login uses
    @Override
    public String getUsername() {
        return email; // or return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}

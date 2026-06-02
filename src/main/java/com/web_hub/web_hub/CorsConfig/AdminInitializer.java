package com.web_hub.web_hub.CorsConfig;

import com.web_hub.web_hub.role.Role;
import com.web_hub.web_hub.user.model.User;
import com.web_hub.web_hub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.password}")
    private String password;

    @Override
    public void run(String... args) {

        // ✅ Check BOTH email and username to prevent database unique constraint violations
        if (userRepository.findByEmail(email).isPresent() || userRepository.findByUsername("admin").isPresent()) {
            System.out.println("ℹ️ Default ADMIN user or username 'admin' already exists. Skipping initialization.");
            return;
        }

        User superAdmin = User.builder()
                .username("admin")
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.ADMIN)

                /* =========================
                   ACCOUNT STATUS
                ========================= */
                .approved(true)                 // 👑 System-approved
                .active(true)                   // 🔓 Can log in
                .emailVerified(true)

                /* =========================
                   SECURITY FLAGS
                ========================= */
                .passwordSet(true)              // ✅ Password exists
                .forcePasswordChange(true)      // 🔐 MUST change on first login
                .failedLoginAttempts(0)
                .lockedUntil(null)

                /* =========================
                   ONBOARDING (SYSTEM USER)
                ========================= */
                .onboardingCompleted(true)

                /* =========================
                   AUDIT / META
                ========================= */
                .createdAt(Instant.now())
                .updatedAt(Instant.now())

                .build();

        userRepository.save(superAdmin);

        System.out.println("✅ Default  ADMIN created: " + email);
        System.out.println("🔐 Password change required on first login");
    }
}
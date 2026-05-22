package com.web_hub.web_hub.jwt;

import com.web_hub.web_hub.CorsConfig.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CorsConfigurationSource corsConfigurationSource; // Injected from your CorsConfig bean

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) // Hook up CORS
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public Auth Endpoints (Merged)
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/self-register",
                                "/api/auth/refresh",
                                "/api/auth/refresh-token",
                                "/api/auth/verify-mfa",
                                "/api/auth/forgot-password",
                                "/api/auth/verify-reset-otp",
                                "/api/auth/reset-password-with-token",
                                "/api/auth/setup-password",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",
                                "/api/auth/verify-email",
                                "/api/auth/complete-registration"
                        ).permitAll()

                        // Protected Endpoints (From your original config)
                        .requestMatchers(HttpMethod.POST, "/api/leaves").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/leaves/employee/**").authenticated()
                        .requestMatchers("/api/payroll/**").authenticated()
                        .requestMatchers(
                                "/api/performance/**",
                                "/api/employees/**",
                                "/api/projects/**",
                                "/api/headcount/**",
                                "/api/milestones/**",
                                "/api/my-profile/**",
                                "/api/trainings/my/**"
                        ).authenticated()

                        // Role-Based Endpoints
                        .requestMatchers("/api/leaves/*/approve").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/leaves/*/reject").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/leaves/status/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/employees/payslips/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers("/api/employees/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/leaves").hasAuthority("ROLE_ADMIN")

                        // Restrict admin routes
                        .requestMatchers(
                                "/api/auth/users/**",
                                "/api/auth/invite",
                                "/api/admin/**"
                        ).hasAuthority("ROLE_ADMIN")

                        .requestMatchers("/api/timesheets").hasAnyAuthority("ROLE_ADMIN", "ROLE_HR", "ROLE_USER")
                        .requestMatchers("/api/timesheets/project/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_HR")
                        .requestMatchers("/api/timesheets/employee/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_HR", "ROLE_USER")
                        .requestMatchers("/api/timesheets/my").permitAll()
                        .requestMatchers("/api/timesheets/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_HR")

                        .requestMatchers("/api/support/all").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/support/tickets").authenticated()
                        .requestMatchers("/api/support/tickets/*/status").hasAuthority("ROLE_ADMIN")

                        .requestMatchers("/api/trainings").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/trainings/assign").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/departments/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/audit-logs").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/assets").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/announcements").hasAuthority("ROLE_ADMIN")

                        .requestMatchers("/api/trainings/complete/**").authenticated()
                        .requestMatchers("/api/certifications/issue").hasAnyAuthority("ROLE_ADMIN", "ROLE_HR")
                        .requestMatchers("/api/certifications/employee/**").authenticated()

                        // Catch-all
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public org.springframework.security.authentication.AuthenticationManager authenticationManager(
            org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
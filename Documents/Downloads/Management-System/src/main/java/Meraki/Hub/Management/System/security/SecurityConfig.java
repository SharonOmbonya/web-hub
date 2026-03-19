package Meraki.Hub.Management.System.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final UserDetailsService userDetailsService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF for stateless applications (e.g., APIs)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // Allow preflight requests for CORS
                        .requestMatchers(
                                "/api/auth/**",  // Public API endpoints (Authentication routes)
                                "/api/admin/register/**",  // Admin registration
                                "/api/admin/login/**",  // Admin login
                                "/swagger-ui/**",  // Swagger UI for API documentation
                                "/v3/api-docs/**",  // Swagger API docs
                                "/h2-console/**"  // H2 Database Console (for testing purposes)
                        ).permitAll()
                        .anyRequest().authenticated()  // Secure all other endpoints, requiring authentication
                )

                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))  // Allow H2 console access

                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless authentication (JWT)

                .authenticationProvider(authenticationProvider())  // Add custom authentication provider

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter before standard username/password authentication

        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return
        RoleHierarchyImpl.fromHierarchy("""
                       
                ADMIN > permission:create
        ADMIN > permission:read
        ADMIN > permission:update
        ADMIN > permission:delete
        ADMIN > user:approve
        HR_MANAGER > permission:read
        HR_MANAGER > user:view
        DEPARTMENT_HEAD > permission:read
        DEPARTMENT_HEAD > user:view
        EMPLOYEE > user:view
         """);  // Define the hierarchy for roles and permissions
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    // Remove ROLE_ prefix from roles in Spring Security
    @Bean
    public GrantedAuthorityDefaults removeRolePrefix() {
        return new GrantedAuthorityDefaults("");  // Custom prefix removal for roles
    }

    // Custom Authentication Provider using UserDetailsService and PasswordEncoder
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Password encoder bean for hashing passwords (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Using BCrypt for secure password storage
    }

    // Authentication manager for managing authentication workflows
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

package Meraki.Hub.Management.System.security;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        log.info("➡ Incoming request: {}", path);

        // PUBLIC ENDPOINTS
        List<String> publicPrefixes = List.of(
                "/api/auth",
                "/api/admin/register",
                "/api/admin/login",
                "/swagger-ui",
                "/v3/api-docs",
                "/h2-console"
        );

        boolean isPublic = publicPrefixes.stream().anyMatch(path::startsWith);

        if (isPublic) {
            log.info("Public endpoint detected → JWT SKIPPED: {}", path);
            chain.doFilter(request, response);
            return;
        }

        // Extract the Authorization header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null) {
            log.warn(" No Authorization header found for: {}", path);
            chain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            log.warn(" Invalid Authorization header format: {}", authHeader);
            chain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String username;

        try {
            username = jwtService.extractUsername(jwt);
            log.info(" Extracted username from JWT: {}", username);
        } catch (JwtException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            chain.doFilter(request, response);
            return;
        }

        // If username exists & user is not authenticated yet
        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info("👤 Loaded user details for: {}", username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    log.info(" JWT is valid → Authenticating user: {}", username);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                } else {
                    log.warn(" JWT validation failed for user: {}", username);
                }

            } catch (Exception ex) {
                log.error(" Error loading user details for {} → {}", username, ex.getMessage());
            }
        }

        chain.doFilter(request, response);
    }
}

package Meraki.Hub.Management.System.auth.api.controller;

import Meraki.Hub.Management.System.auth.api.dto.AuthDTO;
import Meraki.Hub.Management.System.auth.api.dto.AuthResponse;
import Meraki.Hub.Management.System.auth.api.dto.RegisterAdminResponse;
import Meraki.Hub.Management.System.auth.api.dto.RegisterRequest;
import Meraki.Hub.Management.System.security.JwtService;
import Meraki.Hub.Management.System.user.model.User;
import Meraki.Hub.Management.System.user.repository.UserRepository;
import Meraki.Hub.Management.System.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final JwtService jwtService;

    // ADMIN approves user
    @PreAuthorize("hasAuthority('user:approve')")
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveUser(@PathVariable Long id) {

        log.info(" Admin request → Approve user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(" Approve failed → User {} not found", id);
                    return new RuntimeException("User not found");
                });

        user.setApproved(true);
        userRepository.save(user);

        log.info("User {} successfully approved", id);
        return ResponseEntity.ok("User approved");
    }

    // Admin self-registration
    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequest request) {
        RegisterAdminResponse response = authService.registerAdmin(request);
        return ResponseEntity.ok(response);
    }

    // Admin login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthDTO request) {

        log.info(" Admin Login Attempt → email={}", request.getEmail());

        AuthResponse response = authService.adminLogin(request);

        log.info(" Admin {} logged in successfully", request.getEmail());
        return ResponseEntity.ok(response);
    }

}

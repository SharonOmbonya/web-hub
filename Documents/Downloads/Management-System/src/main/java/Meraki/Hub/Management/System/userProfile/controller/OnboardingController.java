package Meraki.Hub.Management.System.userProfile.controller;

import Meraki.Hub.Management.System.security.JwtService;
import Meraki.Hub.Management.System.user.model.User;
import Meraki.Hub.Management.System.user.repository.UserRepository;
import Meraki.Hub.Management.System.userProfile.UserProfileRepository;
import Meraki.Hub.Management.System.userProfile.model.UserProfileModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    /**
     * ✅ Save onboarding details based on user role
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveOnboarding(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody Map<String, Object> body
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token missing");
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user already has a profile
        UserProfileModel userProfile = userProfileRepository.findByUser(user)
                .orElse(UserProfileModel.builder().user(user).role(user.getRole().toString()).build());

        // Map role-specific fields dynamically
        if (user.getRole().toString().equals("ADMIN")) {
            userProfile.setAdminSection((String) body.get("adminSection"));
        } else if (user.getRole().toString().equals("HR_MANAGER")) {
            userProfile.setDepartmentAssigned((String) body.get("departmentAssigned"));
            userProfile.setTeamMembers((String) body.get("teamMembers"));
        } else if (user.getRole().toString().equals("DEPARTMENT_HEAD")) {
            userProfile.setDepartmentName((String) body.get("departmentName"));
            userProfile.setReportingManager((String) body.get("reportingManager"));
        } else if (user.getRole().equals("EMPLOYEE")) {
            userProfile.setJobTitle((String) body.get("jobTitle"));
            userProfile.setStartDate((String) body.get("startDate"));
            userProfile.setSalary((String) body.get("salary"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Unsupported role for onboarding: " + user.getRole());
        }

        userProfileRepository.save(userProfile);

        return ResponseEntity.ok(Map.of(
                "message", "Onboarding details saved successfully",
                "userId", user.getId(),
                "role", user.getRole(),
                "profileId", userProfile.getId()
        ));
    }
}

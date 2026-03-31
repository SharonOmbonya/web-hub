package com.web_hub.web_hub.user;

import com.web_hub.web_hub.employeemodule.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService employeeService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        return ResponseEntity.ok(employeeService.getMyProfile());
    }
}

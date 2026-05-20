package com.web_hub.web_hub.employeemodule.timesheet;

import com.web_hub.web_hub.user.User;
import com.web_hub.web_hub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/my/timesheets")
@RequiredArgsConstructor
public class MyTimesheetController {

    private final MyTimesheetService service;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<?>> getMyTimesheets(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(service.getMyTimesheets(user));
    }
}

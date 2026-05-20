package com.web_hub.web_hub.employeemodule.myprojects;

import com.web_hub.web_hub.projects.model.Project;
import com.web_hub.web_hub.user.User;
import com.web_hub.web_hub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/my/projects")
@RequiredArgsConstructor
public class MyProjectController {

    private final MyProjectService service;
    private final UserRepository userRepository;

    @GetMapping
    public List<Project> getMyProjects(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return service.getMyProjects(user);
    }
}

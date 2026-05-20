package com.web_hub.web_hub.projects.api.controller;

import com.web_hub.web_hub.projects.api.dto.EmployeeProjectResponse;
import com.web_hub.web_hub.projects.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeProjectController {

    private final ProjectService projectService;

    /* ================= GET EMPLOYEE PROJECTS ================= */

    @GetMapping("/{employeeId}/projects")
    public ResponseEntity<List<EmployeeProjectResponse>> getEmployeeProjects(
            @PathVariable Long employeeId
    ) {
        return ResponseEntity.ok(projectService.getEmployeeProjects(employeeId));
    }
}

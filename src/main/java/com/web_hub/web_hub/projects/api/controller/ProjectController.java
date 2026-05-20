package com.web_hub.web_hub.projects.api.controller;
import com.web_hub.web_hub.projects.service.ProjectService;
import com.web_hub.web_hub.projects.api.dto.CreateProjectRequest;
import com.web_hub.web_hub.projects.api.dto.ProjectPLResponse;
import com.web_hub.web_hub.projects.api.dto.ProjectResponse;
import com.web_hub.web_hub.projects.api.dto.ProjectStatusReportResponse;
import com.web_hub.web_hub.projects.resourceutilization.ResourceUtilizationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService service;

    /* ================= CREATE PROJECT ================= */

    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            @RequestBody CreateProjectRequest request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    /* ================= GET ALL ================= */

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    /* ================= ASSIGN EMPLOYEE ================= */

    @PostMapping("/{projectId}/assign/{employeeId}")
    public ResponseEntity<String> assign(
            @PathVariable Long projectId,
            @PathVariable Long employeeId
    ) {
        service.assignEmployee(projectId, employeeId);
        return ResponseEntity.ok("Employee assigned");
    }

    /* ================= GET MEMBERS ================= */

    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<Long>> getMembers(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(service.getProjectMembers(projectId));
    }
    /* ================= PROJECT P&L ================= */

    @GetMapping("/{projectId}/pl")
    public ResponseEntity<ProjectPLResponse> getPL(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(service.getProjectPL(projectId));
    }
    /* ================= RESOURCE UTILIZATION ================= */

    @GetMapping("/{projectId}/utilization")
    public ResponseEntity<List<ResourceUtilizationResponse>> getUtilization(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(service.getResourceUtilization(projectId));
    }
    /* ================= STATUS REPORT ================= */

    @GetMapping("/{projectId}/status-report")
    public ResponseEntity<ProjectStatusReportResponse> getStatusReport(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(service.getStatusReport(projectId));
    }
}

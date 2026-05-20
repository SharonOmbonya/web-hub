package com.web_hub.web_hub.projects.milestones;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/milestones")
@RequiredArgsConstructor
public class MilestoneController {

    private final MilestoneService service;

    /* ================= CREATE ================= */

    @PostMapping
    public ResponseEntity<MilestoneResponse> create(
            @RequestBody CreateMilestoneRequest request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    /* ================= GET BY PROJECT ================= */

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<MilestoneResponse>> getByProject(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(service.getByProject(projectId));
    }

    /* ================= UPDATE STATUS ================= */

    @PutMapping("/{id}/status")
    public ResponseEntity<MilestoneResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }
}

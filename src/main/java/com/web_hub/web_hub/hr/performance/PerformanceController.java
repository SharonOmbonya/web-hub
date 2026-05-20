package com.web_hub.web_hub.hr.performance;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService service;

    /* ================= CREATE ================= */

    @PostMapping
    public ResponseEntity<PerformanceResponse> create(
            @RequestBody CreatePerformanceRequest request
    ) {
        return ResponseEntity.ok(service.createReview(request));
    }

    /* ================= GET ALL ================= */

    @GetMapping
    public ResponseEntity<List<PerformanceResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    /* ================= GET BY EMPLOYEE ================= */

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PerformanceResponse>> getByEmployee(
            @PathVariable Long employeeId
    ) {
        return ResponseEntity.ok(service.getByEmployee(employeeId));
    }
}

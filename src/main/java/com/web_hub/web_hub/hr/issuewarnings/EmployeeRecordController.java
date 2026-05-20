package com.web_hub.web_hub.hr.issuewarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class EmployeeRecordController {

    private final EmployeeRecordService service;

    /* ================= CREATE ================= */

    @PostMapping
    public ResponseEntity<EmployeeRecordResponse> create(
            @RequestBody CreateEmployeeRecordRequest request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    /* ================= GET ALL ================= */

    @GetMapping
    public ResponseEntity<List<EmployeeRecordResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    /* ================= GET BY EMPLOYEE ================= */

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeRecordResponse>> getByEmployee(
            @PathVariable Long employeeId
    ) {
        return ResponseEntity.ok(service.getByEmployee(employeeId));
    }

    /* ================= GET WARNINGS ================= */

    @GetMapping("/warnings")
    public ResponseEntity<List<EmployeeRecordResponse>> getWarnings() {
        return ResponseEntity.ok(service.getWarnings());
    }

    /* ================= GET COMMENDATIONS ================= */

    @GetMapping("/commendations")
    public ResponseEntity<List<EmployeeRecordResponse>> getCommendations() {
        return ResponseEntity.ok(service.getCommendations());
    }
}

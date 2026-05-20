package com.web_hub.web_hub.timesheets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timesheets")
@RequiredArgsConstructor
public class TimesheetController {

    private final TimesheetService service;


    /* ================= CREATE ================= */

    @PostMapping
    public ResponseEntity<TimesheetResponse> create(
            @RequestBody CreateTimesheetRequest request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    /* ================= GET BY PROJECT ================= */

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TimesheetResponse>> getByProject(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(service.getByProject(projectId));
    }

    /* ================= GET BY EMPLOYEE ================= */

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TimesheetResponse>> getByEmployee(
            @PathVariable Long employeeId
    ) {
        return ResponseEntity.ok(service.getByEmployee(employeeId));
    }
}

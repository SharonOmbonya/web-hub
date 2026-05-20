package com.web_hub.web_hub.hr.leave;

import com.web_hub.web_hub.employeemodule.dto.LeaveRequest;
import com.web_hub.web_hub.employeemodule.dto.LeaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    /* ================= EMPLOYEE: APPLY LEAVE ================= */

    @PostMapping
    public ResponseEntity<LeaveResponse> applyLeave(
            @RequestBody LeaveRequest req
    ) {
        return ResponseEntity.ok(leaveService.apply(req));
    }

    /* ================= EMPLOYEE: MY LEAVES ================= */

    @GetMapping("/my")
    public ResponseEntity<List<LeaveResponse>> myLeaves() {
        return ResponseEntity.ok(leaveService.getMyLeaves());
    }

    /* ================= HR: VIEW ALL LEAVES ================= */

    @GetMapping
    public ResponseEntity<List<LeaveResponse>> getAllLeaves() {
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    /* ================= HR: APPROVE LEAVE ================= */

    @PutMapping("/{id}/approve")
    public ResponseEntity<LeaveResponse> approveLeave(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.approveLeave(id));
    }

    /* ================= HR: REJECT LEAVE ================= */

    @PutMapping("/{id}/reject")
    public ResponseEntity<LeaveResponse> rejectLeave(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.rejectLeave(id));
    }

    /* ================= HR: SINGLE LEAVE (OPTIONAL) ================= */

    @GetMapping("/{id}")
    public ResponseEntity<LeaveResponse> getLeaveById(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.getById(id));
    }
}

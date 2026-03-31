package com.web_hub.web_hub.hr.payroll;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping("/run")
    public ResponseEntity<PayrollResponse> runPayroll(
            @RequestBody CreatePayrollRequest request
    ) {
        return ResponseEntity.ok(payrollService.runPayroll(request));
    }

    @GetMapping
    public ResponseEntity<List<PayrollResponse>> getAllPayroll() {
        return ResponseEntity.ok(payrollService.getAllPayroll());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PayrollResponse>> getByEmployee(
            @PathVariable Long employeeId
    ) {
        return ResponseEntity.ok(payrollService.getByEmployee(employeeId));
    }
}

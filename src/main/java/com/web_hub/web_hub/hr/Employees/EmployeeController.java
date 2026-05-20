package com.web_hub.web_hub.hr.Employees;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /* ================= CREATE ================= */

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @RequestBody CreateEmployeeRequest request
    ) {
        return ResponseEntity.ok(employeeService.createEmployee(request));
    }

    /* ================= GET ALL ================= */

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    /* ================= GET BY ID ================= */

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    /* ================= UPDATE ================= */

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @RequestBody CreateEmployeeRequest request
    ) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    /* ================= DELETE ================= */

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted");
    }

    /* ================= SUSPEND ================= */

    @PatchMapping("/{id}/suspend")
    public ResponseEntity<String> suspendEmployee(@PathVariable Long id) {
        employeeService.suspendEmployee(id);
        return ResponseEntity.ok("Employee suspended");
    }



}

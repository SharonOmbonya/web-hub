package com.web_hub.web_hub.departments;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;


    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(
            @RequestBody DepartmentRequest request
    ) {
        return ResponseEntity.ok(departmentService.createDepartment(request));
    }


    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }


    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable Long id,
            @RequestBody DepartmentRequest request
    ) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> archiveDepartment(@PathVariable Long id) {
        departmentService.archiveDepartment(id);
        return ResponseEntity.ok("Department archived");
    }
}

package com.web_hub.web_hub.departments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    /* ================= CREATE ================= */
    public DepartmentResponse createDepartment(DepartmentRequest request) {

        Department department = new Department();

        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setStatus("ACTIVE");
        department.setCreatedAt(LocalDateTime.now());

        Department saved = departmentRepository.save(department);

        return mapToResponse(saved);
    }

    /* ================= GET ALL ================= */
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /* ================= GET BY ID ================= */
    public DepartmentResponse getDepartmentById(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        return mapToResponse(dept);
    }

    /* ================= UPDATE ================= */
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {

        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        dept.setName(request.getName());
        dept.setDescription(request.getDescription());

        Department updated = departmentRepository.save(dept);

        return mapToResponse(updated);
    }

    /* ================= DELETE (ARCHIVE) ================= */
    public void archiveDepartment(Long id) {

        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        dept.setStatus("INACTIVE");

        departmentRepository.save(dept);
    }

    /* ================= MAPPER ================= */
    private DepartmentResponse mapToResponse(Department dept) {
        return DepartmentResponse.builder()
                .id(dept.getId())
                .name(dept.getName())
                .description(dept.getDescription())
                .status(dept.getStatus())
                .build();
    }
}

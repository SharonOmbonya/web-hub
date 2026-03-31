package com.web_hub.web_hub.hr.Employees;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    /* ================= CREATE ================= */

    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {

        validateDepartment(request.getDepartment());

        Employee employee = new Employee();

        employee.setUserId(request.getUserId());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPosition(request.getPosition());
        employee.setDepartment(request.getDepartment());
        employee.setSalary(request.getSalary());

        employee.setStatus("ACTIVE");

        Employee saved = employeeRepository.save(employee);

        return mapToResponse(saved);
    }

    /* ================= GET ALL ================= */

    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /* ================= GET BY ID ================= */

    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return mapToResponse(employee);
    }

    /* ================= UPDATE ================= */

    public EmployeeResponse updateEmployee(Long id, CreateEmployeeRequest request) {

        validateDepartment(request.getDepartment());

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setUserId(request.getUserId());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPosition(request.getPosition());
        employee.setDepartment(request.getDepartment());
        employee.setSalary(request.getSalary());
        employee.setPhone(request.getPhone());

        Employee updated = employeeRepository.save(employee);

        return mapToResponse(updated);
    }

    /* ================= DELETE ================= */

    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employeeRepository.delete(employee);
    }

    /* ================= SUSPEND ================= */

    public void suspendEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setStatus("SUSPENDED");

        employeeRepository.save(employee);
    }

    /* ================= GET DEPARTMENTS ================= */

    public List<String> getDepartments() {
        return List.of(
                "LEADERSHIP",
                "ENGINEERING",
                "PROJECT_MANAGEMENT",
                "QA",
                "SUPPORT",
                "SALES",
                "FINANCE",
                "HR"
        );
    }

    /* ================= VALIDATION ================= */

    private void validateDepartment(String department) {

        if (!getDepartments().contains(department)) {
            throw new RuntimeException("Invalid department: " + department);
        }
    }

    /* ================= MAPPER ================= */

    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .userId(employee.getUserId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .position(employee.getPosition())
                .department(employee.getDepartment())
                .salary(employee.getSalary())
                .status(employee.getStatus())
                .build();
    }
}

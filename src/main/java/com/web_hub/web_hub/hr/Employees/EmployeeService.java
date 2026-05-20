package com.web_hub.web_hub.hr.Employees;
import com.web_hub.web_hub.admin.auditlog.AuditLogService;
import com.web_hub.web_hub.departments.Department;
import com.web_hub.web_hub.departments.DepartmentRepository;
import com.web_hub.web_hub.user.User;
import com.web_hub.web_hub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository EmployeeRepository;
    private final UserRepository UserRepository;
   private final DepartmentRepository departmentRepository;
    private final AuditLogService auditLogService;

    /* ================= CREATE ================= */
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {

        User user = UserRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (EmployeeRepository.existsByUser(user)) {
            throw new RuntimeException("Employee already exists for this user");
        }
        Employee employee = new Employee();

        // link user
        employee.setUser(user);

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPosition(request.getPosition());
        employee.setDepartment(request.getDepartment());
        employee.setSalary(request.getSalary());
        employee.setPhone(request.getPhone());

        employee.setStatus("ACTIVE");
        employee.setCreatedAt(LocalDateTime.now());

        Employee saved = EmployeeRepository.save(employee);
        auditLogService.logAction(
                "CREATE",
                "Employee",
                saved.getId(),
                saved.getEmail(),
                "HR",
                "Created new employee"
        );

        return mapToResponse(saved);
    }

    /* ================= GET ALL ================= */
    public List<EmployeeResponse> getAllEmployees() {
        return EmployeeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /* ================= GET BY ID ================= */
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = EmployeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return mapToResponse(employee);
    }

    /* ================= UPDATE ================= */
    public EmployeeResponse updateEmployee(Long id, CreateEmployeeRequest request) {

        Employee employee = EmployeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Validate department from database
        Department department = departmentRepository.findByName(request.getDepartment())
                .orElseThrow(() -> new RuntimeException("Invalid or non-existing department"));

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPosition(request.getPosition());
        employee.setDepartment(department.getName()); // if using String
        employee.setSalary(request.getSalary());
        employee.setPhone(request.getPhone());

        Employee updated = EmployeeRepository.save(employee);
        auditLogService.logAction(
                "CREATE",
                "Employee",
                updated.getId(),
                updated.getEmail(),
                "HR",
                "Created new employee"
        );

        return mapToResponse(updated);
    }
    /* ================= DELETE ================= */
    public void deleteEmployee(Long id) {
        Employee employee = EmployeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));


        EmployeeRepository.delete(employee);
        auditLogService.logAction(
                "DELETE",
                "Employee",
                employee.getId(),
                employee.getEmail(),
                "HR",
                "Deleted employee"
        );

    }

    /* ================= SUSPEND ================= */
    public void suspendEmployee(Long id) {
        Employee employee = EmployeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setStatus("SUSPENDED");

        EmployeeRepository.save(employee);
        auditLogService.logAction(
                "SUSPEND",
                "Employee",
                employee.getId(),
                employee.getEmail(),
                "HR",
                "Suspended employee"
        );

    }

    public EmployeeResponse getMyProfile(User user) {

        Employee employee = EmployeeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Employee profile not found"));

        return mapToResponse(employee);
    }



    /* ================= MAPPER ================= */
    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .userId(employee.getUser() != null ? employee.getUser().getId() : null)
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .position(employee.getPosition())
                .department(employee.getDepartment())
                .salary(employee.getSalary())
                .phone (employee.getPhone())
                .status(employee.getStatus())
                .phone(employee.getPhone())
                .build();
    }
}

package com.web_hub.web_hub.hr.Employees.service;

import com.web_hub.web_hub.admin.auditlog.AuditLogService;
import com.web_hub.web_hub.departments.model.Department;
import com.web_hub.web_hub.departments.repository.DepartmentRepository;
import com.web_hub.web_hub.hr.Employees.api.dto.CreateEmployeeRequest;
import com.web_hub.web_hub.hr.Employees.model.Employee;
import com.web_hub.web_hub.hr.Employees.model.EmployeeStatus;
import com.web_hub.web_hub.hr.Employees.repository.EmployeeRepository;
import com.web_hub.web_hub.hr.Employees.api.dto.EmployeeResponse;
import com.web_hub.web_hub.exception.ResourceNotFoundException; // 👈 IMPORTED CUSTOM EXCEPTION
import com.web_hub.web_hub.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final AuditLogService auditLogService;

    /* ================= CREATE ================= */
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        // 👇 Changed to IllegalStateException (Caught by your GlobalHandler block #8)
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("An employee with email '" + request.getEmail() + "' already exists");
        }

        Employee employee = new Employee();
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setJobTitle(request.getJobTitle());
        employee.setDepartment(request.getDepartment());
        employee.setDepartmentId(request.getDepartmentId());

        // Onboarding / HR fields
        employee.setStartDate(request.getStartDate());
        employee.setIdentificationNumber(request.getIdentificationNumber());
        employee.setIdType(request.getIdType());
        employee.setNationality(request.getNationality());

        // Financial / KRA fields
        employee.setBankName(request.getBankName());
        employee.setAccountNumber(request.getAccountNumber());
        employee.setKraPin(request.getKraPin());

        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setCreatedAt(LocalDateTime.now());

        Employee saved = employeeRepository.save(employee);

        auditLogService.logAction(
                "CREATE",
                "Employee",
                saved.getId(),
                saved.getEmail(),
                "HR",
                "Created new employee profile"
        );

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
        // 👇 Changed to ResourceNotFoundException (Caught by your GlobalHandler block #7)
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        return mapToResponse(employee);
    }

    /* ================= UPDATE ================= */
    public EmployeeResponse updateEmployee(Long id, CreateEmployeeRequest request) {
        // 👇 Changed to ResourceNotFoundException
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (request.getFirstName() != null) {
            employee.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            employee.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            employee.setEmail(request.getEmail());
        }
        if (request.getJobTitle() != null) {
            employee.setJobTitle(request.getJobTitle());
        }
        if (request.getPhone() != null) {
            employee.setPhone(request.getPhone());
        }

        if (request.getDepartment() != null) {
            // 👇 Changed to ResourceNotFoundException
            Department department = departmentRepository.findByName(request.getDepartment())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with name: " + request.getDepartment()));
            employee.setDepartment(department.getName());
        }
        if (request.getDepartmentId() != null) {
            employee.setDepartmentId(request.getDepartmentId());
        }

        if (request.getStartDate() != null) {
            employee.setStartDate(request.getStartDate());
        }
        if (request.getIdentificationNumber() != null) {
            employee.setIdentificationNumber(request.getIdentificationNumber());
        }
        if (request.getIdType() != null) {
            employee.setIdType(request.getIdType());
        }
        if (request.getNationality() != null) {
            employee.setNationality(request.getNationality());
        }

        if (request.getBankName() != null) {
            employee.setBankName(request.getBankName());
        }
        if (request.getAccountNumber() != null) {
            employee.setAccountNumber(request.getAccountNumber());
        }
        if (request.getKraPin() != null) {
            employee.setKraPin(request.getKraPin());
        }

        Employee updated = employeeRepository.save(employee);

        auditLogService.logAction(
                "UPDATE",
                "Employee",
                updated.getId(),
                updated.getEmail(),
                "HR",
                "Updated employee details"
        );

        return mapToResponse(updated);
    }

    /* ================= DELETE ================= */
    public void deleteEmployee(Long id) {
        // 👇 Changed to ResourceNotFoundException
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employeeRepository.delete(employee);

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
    public EmployeeResponse suspendEmployee(Long id) {
        // 👇 Changed to ResourceNotFoundException
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employee.setStatus(EmployeeStatus.SUSPENDED);
        Employee saved = employeeRepository.save(employee);

        auditLogService.logAction(
                "SUSPEND",
                "Employee",
                saved.getId(),
                saved.getEmail(),
                "HR",
                "Suspended employee account"
        );

        return mapToResponse(saved);
    }

    /* ================= GET PROFILE FROM USER ================= */
    public EmployeeResponse getMyProfile(User user) {
        // 👇 Changed to ResourceNotFoundException
        Employee employee = employeeRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No employee profile found for email: " + user.getEmail()));

        return mapToResponse(employee);
    }

    /* ================= DEACTIVATE ================= */
    public EmployeeResponse deactivateEmployee(Long id) {
        // 👇 Changed to ResourceNotFoundException
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employee.setStatus(EmployeeStatus.INACTIVE);
        Employee saved = employeeRepository.save(employee);

        auditLogService.logAction(
                "DEACTIVATE",
                "Employee",
                saved.getId(),
                saved.getEmail(),
                "HR",
                "Deactivated employee account"
        );

        return mapToResponse(saved);
    }

    /* ================= REACTIVATE ================= */
    public EmployeeResponse reactivateEmployee(Long id) {
        // 👇 Changed to ResourceNotFoundException
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employee.setStatus(EmployeeStatus.ACTIVE);
        Employee saved = employeeRepository.save(employee);

        auditLogService.logAction(
                "REACTIVATE",
                "Employee",
                saved.getId(),
                saved.getEmail(),
                "HR",
                "Reactivated employee account"
        );

        return mapToResponse(saved);
    }

    /* ================= MAPPER ================= */
    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .jobTitle(employee.getJobTitle())
                .department(employee.getDepartment())
                .departmentId(employee.getDepartmentId())
                .status(employee.getStatus() != null ? employee.getStatus().name() : null)

                .startDate(employee.getStartDate())
                .identificationNumber(employee.getIdentificationNumber())
                .idType(employee.getIdType())
                .nationality(employee.getNationality())

                .bankName(employee.getBankName())
                .accountNumber(employee.getAccountNumber())
                .kraPin(employee.getKraPin())

                .createdAt(employee.getCreatedAt())
                .build();
    }
}
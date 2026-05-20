package com.web_hub.web_hub.hr.Headcount;

import com.web_hub.web_hub.hr.Employees.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeadcountService {

    private final EmployeeRepository employeeRepository;

    public HeadcountResponse getSummary() {

        long total = employeeRepository.count();

        long active = employeeRepository.countByStatus("ACTIVE");

        long inactive = employeeRepository.countByStatus("INACTIVE");

        return HeadcountResponse.builder()
                .totalEmployees(total)
                .activeEmployees(active)
                .inactiveEmployees(inactive)
                .build();
    }
}

package com.web_hub.web_hub.employeemodule.payslip;

import com.web_hub.web_hub.hr.payroll.Payroll;
import com.web_hub.web_hub.hr.payroll.PayrollRepository;
import com.web_hub.web_hub.hr.Employees.Employee;
import com.web_hub.web_hub.hr.Employees.EmployeeRepository;
import com.web_hub.web_hub.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPayslipService {

    private final EmployeeRepository employeeRepository;
    private final PayrollRepository payrollRepository;

    public List<Payroll> getMyPayslips(User user) {

        // Get employee linked to logged-in user
        Employee employee = employeeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Fetch payroll records for that employee
        return payrollRepository.findByEmployeeId(employee.getId());
    }
}

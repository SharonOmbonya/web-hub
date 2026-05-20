package com.web_hub.web_hub.employeemodule.timesheet;

import com.web_hub.web_hub.timesheets.Timesheet;
import com.web_hub.web_hub.timesheets.TimesheetRepository;
import com.web_hub.web_hub.hr.Employees.Employee;
import com.web_hub.web_hub.hr.Employees.EmployeeRepository;
import com.web_hub.web_hub.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyTimesheetService {

    private final EmployeeRepository employeeRepository;
    private final TimesheetRepository timesheetRepository;

    public List<Timesheet> getMyTimesheets(User user) {

        // 🔥 SAME as payslip
        Employee employee = employeeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Fetch timesheets for that employee
        return timesheetRepository.findByEmployeeId(employee.getId());
    }
}

package com.web_hub.web_hub.employeemodule.dto;

import java.time.LocalDate;

public record PayslipResponse(
        Long id,
        Long employeeId,
        String employeeName,
        String payPeriod,
        LocalDate payDate,
        double basicSalary,
        double allowances,
        double deductions,
        double netPay
) {}

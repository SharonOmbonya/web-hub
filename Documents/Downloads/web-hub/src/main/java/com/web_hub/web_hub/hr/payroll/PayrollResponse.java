package com.web_hub.web_hub.hr.payroll;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayrollResponse {

    private Long id;
    private Long employeeId;

    private String month;
    private int year;

    private double grossPay;
    private double netPay;

    private String status;
}

package com.web_hub.web_hub.hr.payroll;

import lombok.Data;

@Data
public class CreatePayrollRequest {

    private Long employeeId;
    private String month;
    private int year;

    private double basicSalary;
    private double houseAllowance;

    private double shif;
    private double housingLevy;
    private double incomeTax;
    private double personalRelief;
    private double paye;
}

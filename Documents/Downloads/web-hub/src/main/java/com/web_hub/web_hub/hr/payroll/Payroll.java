package com.web_hub.web_hub.hr.payroll;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    private String month;
    private int year;

    // Earnings
    private double basicSalary;
    private double houseAllowance;

    // Deductions
    private double shif;
    private double housingLevy;
    private double incomeTax;
    private double personalRelief;
    private double paye;

    // Calculated
    private double grossPay;
    private double taxablePay;
    private double payAfterTax;
    private double netPay;

    private String status; // PENDING / PAID
}

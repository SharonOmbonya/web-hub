package com.web_hub.web_hub.hr.Employees.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Data
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String departmentId;
    private String firstName;
    private String lastName;
    private String email;
    private String startDate;
    private String bankName;
    private String accountNumber;
    private String jobTitle;
    private String department;
    private String kraPin;
    private String identificationNumber;
    private String idType;

    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;

    private String nationality;
    private String phone;
    private LocalDateTime createdAt;
}
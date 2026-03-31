package com.web_hub.web_hub.hr.Employees;

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

    private Long userId;

    private String firstName;
    private String lastName;
    private String email;

    private String position;
    private String department;
    private Double salary;
    private String status; // ACTIVE
    private String phone;

    private LocalDateTime createdAt;
}

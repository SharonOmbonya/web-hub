package com.web_hub.web_hub.hr.Employees;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeResponse {

    private Long id;
    private Long userId;

    private String firstName;
    private String lastName;
    private String department;
    private String email;
    private String phone;
    private String status;
    private String position;
    private Double salary;

    private LocalDateTime createdAt;
}

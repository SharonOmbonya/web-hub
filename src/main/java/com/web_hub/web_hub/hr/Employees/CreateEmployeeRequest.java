package com.web_hub.web_hub.hr.Employees;

import lombok.Data;

@Data
public class CreateEmployeeRequest {

    private Long userId;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String position;
    private String department;
    private Double salary;
}

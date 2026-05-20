package com.web_hub.web_hub.employeemodule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeProjectResponse {
    private String projectName;
    private String status;
    private String progress;
}

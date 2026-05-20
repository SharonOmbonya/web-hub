package com.web_hub.web_hub.timesheets;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTimesheetRequest {

    private Long projectId;
    private Long employeeId;
    private LocalDate workDate;
    private Double hours;
    private String description;
}

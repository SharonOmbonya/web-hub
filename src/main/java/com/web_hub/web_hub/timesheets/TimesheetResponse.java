package com.web_hub.web_hub.timesheets;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TimesheetResponse {

    private Long id;
    private Long projectId;
    private Long employeeId;
    private LocalDate workDate;
    private Double hours;
    private String description;
}

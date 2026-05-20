package com.web_hub.web_hub.hr.performance;

import lombok.Data;

@Data
public class CreatePerformanceRequest {
    private Long employeeId;
    private String reviewPeriod;
    private int rating;
    private String comments;
}

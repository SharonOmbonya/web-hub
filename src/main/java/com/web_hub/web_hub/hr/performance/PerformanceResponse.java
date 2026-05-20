package com.web_hub.web_hub.hr.performance;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PerformanceResponse {
    private Long id;
    private Long employeeId;
    private String reviewPeriod;
    private int rating;
    private String comments;
    private String status;
}

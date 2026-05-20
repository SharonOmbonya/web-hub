package com.web_hub.web_hub.projects.resourceutilization;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResourceUtilizationResponse {

    private Long employeeId;
    private Double totalHours;
    private String status;
}

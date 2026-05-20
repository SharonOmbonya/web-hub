package com.web_hub.web_hub.projects.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectStatusReportResponse {

    private Long projectId;
    private String projectName;
    private String status;

    private int totalMilestones;
    private int completedMilestones;
    private int delayedMilestones;

    private Double totalHours;
}

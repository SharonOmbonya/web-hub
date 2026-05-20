package com.web_hub.web_hub.projects.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectPLResponse {

    private Long projectId;
    private Double budget;
    private Double actualCost;
    private Double profitOrLoss;
}

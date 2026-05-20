package com.web_hub.web_hub.projects.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ProjectResponse {

    private Long id;
    private String name;
    private String client;
    private Double budget;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}

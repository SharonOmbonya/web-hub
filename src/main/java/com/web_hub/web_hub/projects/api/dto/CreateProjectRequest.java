package com.web_hub.web_hub.projects.api.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateProjectRequest {
    private String name;
    private String client;
    private Double budget;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}

package com.web_hub.web_hub.employeemodule.dto;

import lombok.Data;

@Data
public class TicketRequest {
    private String title;
    private String description;
    private String priority;
}

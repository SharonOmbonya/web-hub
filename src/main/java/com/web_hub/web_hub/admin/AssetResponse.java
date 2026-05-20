package com.web_hub.web_hub.admin;

public record AssetResponse(
        Long id,
        String name,
        String type,
        String serialNumber,
        String status,
        String employeeName,
        String employeeEmail
) {}

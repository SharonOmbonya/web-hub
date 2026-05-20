package com.web_hub.web_hub.admin;

import lombok.Data;

@Data
public class AssetRequest {
    private String name;
    private String type;
    private String serialNumber;
    private Long employeeId;
}

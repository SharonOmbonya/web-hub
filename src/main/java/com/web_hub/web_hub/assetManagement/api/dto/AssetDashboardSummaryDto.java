package com.web_hub.web_hub.assetManagement.api.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetDashboardSummaryDto {
    private long totalAssets;
    private long assigned;
    private long available;
    private long inRepair;
}

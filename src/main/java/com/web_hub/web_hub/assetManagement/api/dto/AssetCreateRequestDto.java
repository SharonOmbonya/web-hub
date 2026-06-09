package com.web_hub.web_hub.assetManagement.api.dto;

import com.web_hub.web_hub.assetManagement.model.AssetCondition;
import lombok.Data;

@Data
public class AssetCreateRequestDto {
    private String name;
    private String category;
    private String serialNumber;
    private AssetCondition assetCondition;
}

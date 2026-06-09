package com.web_hub.web_hub.assetManagement.api.dto;


import com.web_hub.web_hub.assetManagement.model.AssetCondition;
import com.web_hub.web_hub.assetManagement.model.AssetStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetResponseDto {
    private Long id;
    private String assetCode;
    private String name;
    private String category;
    private String serialNumber;
    private String assignedToName;
    private String assignedToDepartment;
    private LocalDate assignmentDate;
    private AssetCondition assetCondition;
    private AssetStatus status;
}

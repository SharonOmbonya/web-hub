package com.web_hub.web_hub.role.api.dto;

import com.web_hub.web_hub.permissions.api.dto.PermissionResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolesCreateResponseDto {
    private Long roleId;
    private String roleName;
    private List<PermissionResponseDTO> permissions;
}

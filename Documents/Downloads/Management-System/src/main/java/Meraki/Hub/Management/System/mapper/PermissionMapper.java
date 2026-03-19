package Meraki.Hub.Management.System.mapper;

import Meraki.Hub.Management.System.permissions.api.dto.PermissionsDTO;
import Meraki.Hub.Management.System.permissions.model.PermissionModel;

public class PermissionMapper {

    public static PermissionsDTO toDTO(PermissionModel model) {
        return PermissionsDTO.builder()
                .id(model.getId())
                .name(model.getName())
                .build();
    }

    public static PermissionModel toEntity(PermissionsDTO dto) {
        PermissionModel model = new PermissionModel();
        model.setId(dto.getId());
        model.setName(dto.getName());
        return model;
    }
}

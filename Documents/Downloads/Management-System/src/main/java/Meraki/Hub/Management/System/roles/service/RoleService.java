package Meraki.Hub.Management.System.roles.service;

import Meraki.Hub.Management.System.roles.api.dto.RoleDTO;
import Meraki.Hub.Management.System.roles.api.dto.RoleRequest;

import java.util.List;

public interface RoleService {
    RoleDTO createRole(RoleRequest request);
    RoleDTO updateRole(RoleRequest request);
    RoleDTO getRole(Long id);
    List<RoleDTO> getAllRoles();
}

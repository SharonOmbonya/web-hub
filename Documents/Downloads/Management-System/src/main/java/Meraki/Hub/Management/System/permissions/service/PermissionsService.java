package Meraki.Hub.Management.System.permissions.service;
import Meraki.Hub.Management.System.permissions.api.dto.PermissionsDTO;
import Meraki.Hub.Management.System.permissions.model.PermissionModel;

import java.util.List;

public interface PermissionsService {

    // CREATE
    PermissionModel createPermission(PermissionModel permission);

    // READ (fetch all)
    List<PermissionModel> getAllPermissions();

    // READ (fetch one by ID)
    PermissionModel getPermissionById(Long id);

    // READ (fetch one by name)
    PermissionModel getPermissionByName(String name);

    // UPDATE
    PermissionsDTO updatePermissionName(Long id, String newName);

    // DELETE
    void deletePermissionByName(Long id);


}

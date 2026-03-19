package Meraki.Hub.Management.System.permissions.repository;


import Meraki.Hub.Management.System.permissions.model.PermissionModel;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository {


    Optional<PermissionModel> findByName(String name);


    List<PermissionModel> findAll();

    Optional<PermissionModel> findById(Long id);


    void deleteByName(String name);

    boolean existsByName(String name);

    List<PermissionModel> findPermissionsByRole(String role);

    List<PermissionModel> findPermissionsByRoles(List<String> role);
    List<PermissionModel> findByStatus(String status);

    List<PermissionModel> findByPermissionType(String permissionType);


}

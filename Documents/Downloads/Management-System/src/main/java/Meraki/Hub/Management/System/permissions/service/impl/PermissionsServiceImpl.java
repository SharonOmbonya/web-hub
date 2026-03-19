package Meraki.Hub.Management.System.permissions.service.impl;

import Meraki.Hub.Management.System.exceptions.PermissionAlreadyExistsException;
import Meraki.Hub.Management.System.exceptions.PermissionNotFoundException;
import Meraki.Hub.Management.System.permissions.api.dto.PermissionsDTO;
import Meraki.Hub.Management.System.permissions.model.PermissionModel;
import Meraki.Hub.Management.System.permissions.repository.jpa.PermissionJpaRepository;
import Meraki.Hub.Management.System.permissions.service.PermissionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionsServiceImpl implements PermissionsService {

    private final PermissionJpaRepository permissionJpaRepository;

    @Override
    public PermissionModel createPermission(PermissionModel permission) {

        // Prevent duplicate permission name
        if (permissionJpaRepository.findByName(permission.getName()).isPresent()) {
            throw new PermissionAlreadyExistsException("Permission already exists: " + permission.getName());
        }

        return permissionJpaRepository.save(permission);
    }

    @Override
    public List<PermissionModel> getAllPermissions() {
        return permissionJpaRepository.findAll();
    }

    @Override
    public PermissionModel getPermissionById(Long id) {
        return permissionJpaRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException("Permission not found with ID: " + id));
    }

    @Override
    public PermissionModel getPermissionByName(String name) {
        return permissionJpaRepository.findByName(name)
                .orElseThrow(() -> new PermissionNotFoundException("Permission not found with name: " + name));
    }

    @Override
    public PermissionsDTO updatePermissionName(Long id, String newName) {

        // Prevent duplicate on update
        permissionJpaRepository.findByName(newName).ifPresent(p -> {
            throw new PermissionAlreadyExistsException("Permission already exists: " + newName);
        });

        PermissionModel permission = getPermissionById(id);

        // Update value
        permissionJpaRepository.updatePermissionName(id, newName);

        // Fetch updated record
        PermissionModel updated = permissionJpaRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException("Permission not found after update"));

        // Convert to DTO
        return new PermissionsDTO(updated.getId(), updated.getName());
    }


    @Override
    public void deletePermissionByName(Long id) {
        getPermissionById(id); // ensures name exists
        permissionJpaRepository.deleteById(id);
    }
}

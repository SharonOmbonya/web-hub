package Meraki.Hub.Management.System.roles.service.impl;


import Meraki.Hub.Management.System.permissions.model.PermissionModel;
import Meraki.Hub.Management.System.permissions.repository.jpa.PermissionJpaRepository;
import Meraki.Hub.Management.System.roles.api.dto.RoleDTO;
import Meraki.Hub.Management.System.roles.api.dto.RoleRequest;
import Meraki.Hub.Management.System.roles.model.Role;
import Meraki.Hub.Management.System.roles.repository.RoleRepository;
import Meraki.Hub.Management.System.roles.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionJpaRepository permissionRepository;


    // CREATE ROLE

    @Override
    public RoleDTO createRole(RoleRequest request) {

        // Validate duplicate name
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Role already exists: " + request.getName());
        }

        // Validate permission names
        List<PermissionModel> permissions = permissionRepository.findByNameIn(request.getPermissionNames());
        if (permissions.size() != request.getPermissionNames().size()) {
            throw new RuntimeException("Some permissions do not exist: " + request.getPermissionNames());
        }

        Role role = Role.builder()
                .name(request.getName())
                .permissions(Set.copyOf(permissions))
                .build();

        return toDTO(roleRepository.save(role));
    }

    // UPDATE ROLE
    @Override
    public RoleDTO updateRole(RoleRequest request) {

        Role role = roleRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Role not found"));


        // 1. Update NAME only if provided
        if (request.getName() != null && !request.getName().isBlank()) {


            role.setName(request.getName());
        }

        if (request.getPermissionNames() != null && !request.getPermissionNames().isEmpty()) {

            // Fetch permissions
            List<PermissionModel> permissions =
                    permissionRepository.findAllById(Collections.singleton(request.getId()));

            // Validate missing permissions
            if (permissions.size() != request.getPermissionNames().size()) {
                throw new RuntimeException("Some permissions do not exist: " + request.getPermissionNames());
            }

            role.setPermissions(Set.copyOf(permissions));
        }

        // Save updated role
        return toDTO(roleRepository.save(role));
    }



    // GET ONE ROLE
    @Override
    public RoleDTO getRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return toDTO(role);
    }


    // GET ALL ROLES

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    // MAPPER

    private RoleDTO toDTO(Role role) {

        List<String> permissionNames = role.getPermissions().stream()
                .map(PermissionModel::getName)
                .toList();

        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .permissionNames(permissionNames)
                .build();
    }
}

package com.web_hub.web_hub.role.service;

import com.web_hub.web_hub.exception.ResourceNotFoundException;
import com.web_hub.web_hub.permissions.api.dto.PermissionResponseDTO;
import com.web_hub.web_hub.permissions.model.PermissionsModel;
import com.web_hub.web_hub.permissions.repository.PermissionRepository;
import com.web_hub.web_hub.role.api.dto.RolesCreateRequestDto;
import com.web_hub.web_hub.role.api.dto.RolesCreateResponseDto;
import com.web_hub.web_hub.role.model.RolesModel;
import com.web_hub.web_hub.role.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    // CREATE
    @Transactional
    public RolesCreateResponseDto createRole(RolesCreateRequestDto dto) {
        // Check if the role name is already taken globally
        if (roleRepository.existsByRoleName(dto.getRoleName())) {
            throw new DataIntegrityViolationException("Role with name '" + dto.getRoleName() + "' already exists");
        }

        RolesModel role = new RolesModel();
        role.setRoleName(dto.getRoleName());

        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            List<PermissionsModel> perms = permissionRepository.findAllByIdIn(dto.getPermissionIds());
            long uniqueRequestedCount = dto.getPermissionIds().stream().distinct().count();

            if (perms.size() != uniqueRequestedCount) {
                throw new ResourceNotFoundException("One or more provided permission IDs do not exist.");
            }
            role.setPermissions(new HashSet<>(perms));
        } else {
            role.setPermissions(new HashSet<>());
        }

        return mapToResponse(roleRepository.save(role));
    }

    // UPDATE
    @Transactional
    public RolesCreateResponseDto updateRole(Long id, RolesCreateRequestDto dto) {
        RolesModel role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        if (dto.getRoleName() != null && !dto.getRoleName().trim().isEmpty()) {
            // Check if another role record is already using this updated name
            if (roleRepository.existsByRoleNameAndRoleIdNot(dto.getRoleName(), id)) {
                throw new DataIntegrityViolationException("Role with name '" + dto.getRoleName() + "' already exists on another record");
            }
            role.setRoleName(dto.getRoleName());
        }

        if (dto.getPermissionIds() != null) {
            if (dto.getPermissionIds().isEmpty()) {
                role.getPermissions().clear();
            } else {
                List<PermissionsModel> perms = permissionRepository.findAllByIdIn(dto.getPermissionIds());
                long uniqueRequestedCount = dto.getPermissionIds().stream().distinct().count();

                if (perms.size() != uniqueRequestedCount) {
                    throw new ResourceNotFoundException("One or more provided permission IDs do not exist.");
                }
                role.setPermissions(new HashSet<>(perms));
            }
        }

        return mapToResponse(roleRepository.save(role));
    }

    // GET ALL
    public List<RolesCreateResponseDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // GET BY ID
    public RolesCreateResponseDto getRoleById(Long id) {
        RolesModel role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return mapToResponse(role);
    }



    // DELETE
    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }

    // HELPER MAPPING METHOD
    private RolesCreateResponseDto mapToResponse(RolesModel role) {
        List<PermissionResponseDTO> permissionDtos = role.getPermissions() != null
                ? role.getPermissions().stream()
                .map(permission -> {
                    PermissionResponseDTO dto = new PermissionResponseDTO();
                    dto.setId(permission.getId());
                    dto.setPermissionName(permission.getPermissionName());
                    return dto;
                })
                .toList()
                : List.of();

        return new RolesCreateResponseDto(
                role.getRoleId(),     // Matches the lowercase 'roleId' field via Lombok getter
                role.getRoleName(),
                permissionDtos
        );
    }
}
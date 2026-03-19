package Meraki.Hub.Management.System.permissions.api.controller;
import Meraki.Hub.Management.System.mapper.PermissionMapper;
import Meraki.Hub.Management.System.permissions.api.dto.PermissionsDTO;
import Meraki.Hub.Management.System.permissions.model.PermissionModel;
import Meraki.Hub.Management.System.permissions.service.PermissionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionsService permissionsService;

    @PreAuthorize("hasAuthority('permission:create')")
    @PostMapping
    public PermissionsDTO create(@RequestBody PermissionsDTO PermissionsDTO) {
        PermissionModel model = PermissionMapper.toEntity(PermissionsDTO);
        PermissionModel saved = permissionsService.createPermission(model);
        return PermissionMapper.toDTO(saved);
    }

    @PreAuthorize("hasAuthority('permission:read')")
    @GetMapping
    public List<PermissionsDTO> getAll() {
        return permissionsService.getAllPermissions()
                .stream()
                .map(PermissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('permission:read')")
    @GetMapping("/{id}")
    public PermissionsDTO getById(@PathVariable Long id) {
        return PermissionMapper.toDTO(permissionsService.getPermissionById(id));
    }

    @PreAuthorize("hasAuthority('permission:read')")
    @GetMapping("/name/{name}")
    public PermissionsDTO getByName(@PathVariable String name) {
        return PermissionMapper.toDTO(permissionsService.getPermissionByName(name));
    }

    @PreAuthorize("hasAuthority('permission:update')")
    @PutMapping("/{id}")
    public ResponseEntity<PermissionsDTO> updateName(
            @PathVariable Long id,
            @RequestBody PermissionsDTO request
    ) {
        PermissionsDTO updated = permissionsService.updatePermissionName(id, request.getName());
        return ResponseEntity.ok(updated);
    }



    @PreAuthorize("hasAuthority('permission:delete')")
    @DeleteMapping("/{id}")
    public void deleteByName(@PathVariable Long id) {
        permissionsService.deletePermissionByName(id);
    }
}

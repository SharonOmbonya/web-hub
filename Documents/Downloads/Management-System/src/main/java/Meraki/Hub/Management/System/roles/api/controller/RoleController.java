package Meraki.Hub.Management.System.roles.api.controller;
import Meraki.Hub.Management.System.roles.api.dto.RoleDTO;
import Meraki.Hub.Management.System.roles.api.dto.RoleRequest;
import Meraki.Hub.Management.System.roles.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.createRole(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(
            @PathVariable Long id,
            @RequestBody RoleRequest request) {

        request.setId(id);
        RoleDTO updated = roleService.updateRole(request);

        return ResponseEntity.ok(updated);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRole(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRole(id));
    }

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}

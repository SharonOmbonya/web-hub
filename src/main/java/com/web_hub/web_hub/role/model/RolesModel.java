package com.web_hub.web_hub.role.model;


import com.web_hub.web_hub.permissions.model.PermissionsModel;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "roles")
@Data
public class RolesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId; // ◄ Changed from RoleId to roleId

    private String roleName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<PermissionsModel> permissions;
}

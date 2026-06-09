package com.web_hub.web_hub.role.repository;

import com.web_hub.web_hub.permissions.model.PermissionsModel;
import com.web_hub.web_hub.role.model.RolesModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RolesModel, Long> {
    Optional<RolesModel> findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);
    boolean existsByRoleNameAndRoleIdNot(String roleName, Long roleId);
}


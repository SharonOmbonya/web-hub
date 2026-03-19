package Meraki.Hub.Management.System.roles.repository;


import Meraki.Hub.Management.System.roles.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}

package Meraki.Hub.Management.System.permissions.repository.jpa;
import Meraki.Hub.Management.System.permissions.model.PermissionModel;
import Meraki.Hub.Management.System.permissions.repository.PermissionRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionJpaRepository
        extends JpaRepository<PermissionModel, Long>, PermissionRepository {


    Optional<PermissionModel> findByName(String name);

    Optional<PermissionModel> findById(Long id);

    List<PermissionModel> findByNameIn(List<String> names);


    List<PermissionModel> findAll();


    void deleteByName(String name);

    @Modifying
    @Transactional
    @Query("UPDATE PermissionModel p SET p.name = :newName WHERE p.id = :id")
    void updatePermissionName(Long id, String newName);

    @Query("SELECT p FROM PermissionModel p JOIN p.roles r WHERE r.name = :roleName")
    List<PermissionModel> findPermissionsByRole(String roleName);

    // Find permissions by status (ACTIVE/INACTIVE)
    List<PermissionModel> findByStatus(String status);
    // Check if permission exists by name (validation during creation or assignment)
    boolean existsByName(String name);

    // 4. Find permissions by multiple roles (permissions for several roles at once)
    @Query("SELECT p FROM PermissionModel p JOIN p.roles r WHERE r.name IN :roleNames")
    List<PermissionModel> findPermissionsByRoles(List<String> roleNames);

    // 5. Check if a permission exists by ID (useful for permission validation)
    boolean existsById(Long id);

}

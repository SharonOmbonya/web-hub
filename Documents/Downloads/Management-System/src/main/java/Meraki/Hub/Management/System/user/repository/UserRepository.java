package Meraki.Hub.Management.System.user.repository;


import Meraki.Hub.Management.System.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRoleName(String roleName);
    Optional<User> findByRefreshToken(String token);

}

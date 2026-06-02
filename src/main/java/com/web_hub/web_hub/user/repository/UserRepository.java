package com.web_hub.web_hub.user.repository;


import com.web_hub.web_hub.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User>findByUsername(String userName);
    Optional<User> findByRefreshToken(String token);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByResetToken(String resetToken);

   Optional<User>findByInviteToken(String token);
}

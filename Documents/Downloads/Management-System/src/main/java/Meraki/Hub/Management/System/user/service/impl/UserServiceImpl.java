package Meraki.Hub.Management.System.user.service.impl;



import Meraki.Hub.Management.System.roles.model.Role;
import Meraki.Hub.Management.System.roles.repository.RoleRepository;
import Meraki.Hub.Management.System.user.model.User;
import Meraki.Hub.Management.System.user.repository.UserRepository;
import Meraki.Hub.Management.System.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // CREATE USER
    @Override
    public User createUser(User user) {

        // Check for duplicate email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with email: " + user.getEmail());
        }

        // Validate role exists
        if (user.getRole() != null && !roleRepository.existsById(user.getRole().getId())) {
            throw new RuntimeException("Role not found for ID: " + user.getRole().getId());
        }

        return userRepository.save(user);
    }

    // GET USER BY ID
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    // GET USER BY EMAIL
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // GET ALL USERS
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // DELETE USER
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    // GET ROLE BY USER
    @Override
    public Role getRoleByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return user.getRole();
    }

    // GET USERS BY ROLE
    @Override
    public List<User> getUsersByRole(String roleName) {
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isEmpty()) {
            return Collections.emptyList();
        }
        Role role = roleOpt.get();
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() != null && u.getRole().getId().equals(role.getId()))
                .collect(Collectors.toList());
    }

    // UPDATE USER
    @Override
    public User updateUser(User updatedUser) {
        User user = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + updatedUser.getId()));

        if (updatedUser.getFullName() != null) user.setFullName(updatedUser.getFullName());
        if (updatedUser.getEmail() != null) user.setEmail(updatedUser.getEmail());
        if (updatedUser.getPassword() != null) user.setPassword(updatedUser.getPassword());
        if (updatedUser.getRole() != null) {
            Role role = roleRepository.findById(updatedUser.getRole().getId())
                    .orElseThrow(() -> new RuntimeException("Role not found for ID: " + updatedUser.getRole().getId()));
            user.setRole(role);
        }
        user.setApproved(updatedUser.isApproved());

        return userRepository.save(user);
    }
}

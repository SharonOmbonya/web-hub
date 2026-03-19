package Meraki.Hub.Management.System.user.service;
import Meraki.Hub.Management.System.roles.model.Role;
import Meraki.Hub.Management.System.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    User getUserByEmail(String email);

    List<User> getUsersByRole(String role);

    Role getRoleByUser(Long userId);



    void deleteUser(Long id);

    User updateUser(User updatedUser);
}

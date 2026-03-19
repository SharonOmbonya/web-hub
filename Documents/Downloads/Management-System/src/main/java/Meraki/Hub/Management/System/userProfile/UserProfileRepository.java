package Meraki.Hub.Management.System.userProfile;


import Meraki.Hub.Management.System.user.model.User;
import Meraki.Hub.Management.System.userProfile.model.UserProfileModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfileModel, Long> {
    Optional<UserProfileModel> findByUser(User user);
}

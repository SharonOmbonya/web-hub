package Meraki.Hub.Management.System.security;

import Meraki.Hub.Management.System.roles.model.Role;
import Meraki.Hub.Management.System.user.model.User;
import Meraki.Hub.Management.System.user.repository.UserRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Role role = user.getRole();
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        // Add the role itself (ADMIN or USER)
        authorities.add(new SimpleGrantedAuthority(role.getName()));

        // ADMIN override — automatic full privileges
        if (role.getName().equalsIgnoreCase("ADMIN")) {
            authorities.add(new SimpleGrantedAuthority("ALL_PRIVILEGES"));
        }

        // Add permissions assigned in DB (for normal users)
        role.getPermissions().forEach(permission ->
                authorities.add(new SimpleGrantedAuthority(permission.getName()))
        );

        // Return Spring Security UserDetails
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}

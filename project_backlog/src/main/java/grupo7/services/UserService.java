package grupo7.services;

import grupo7.models.AppUser;
import grupo7.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Leer todos los usuarios
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    // Leer un usuario por ID
    public Optional<AppUser> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public AppUser saveUser(AppUser user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            AppUser existingUser = userRepository.findById(user.getId()).orElseThrow();
            user.setPassword(existingUser.getPassword());
        }
        return userRepository.save(user);
    }

    //Borrar un usuario
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Metodo de UserDetailsService
    public UserDetails loadUserByUsername(String username) {
        AppUser user = userRepository.findByUsername(username).orElseThrow();
        List<GrantedAuthority> authorities = new ArrayList<>();

        // If user has role ADMINISTRATOR, add "ROLE_ADMINISTRATOR" as an authority
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}

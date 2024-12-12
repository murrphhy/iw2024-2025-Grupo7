package grupo7.services;

import grupo7.models.AppUser;
import grupo7.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Configuration
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Crear un nuevo usuario
    public AppUser createUser(AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encripta la contraseña
        return userRepository.save(user);
    }

    // Leer todos los usuarios
    public List<AppUser> getUsers() {
        return userRepository.findAll();
    }

    // Leer un usuario por ID
    public Optional<AppUser> getUserByID(Long id) {
        return userRepository.findById(id);
    }

    // Método de UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        System.out.println("Usuario encontrado: " + appUser); // Log para depurar

        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .build();
    }
}

package grupo7.services;

import grupo7.models.AppUser;
import grupo7.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Leer todos los usuarios
    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

    // Leer un usuario por ID
    public Optional<AppUser> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Crear y guardar un usuario
    public AppUser saveUser(AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encripta la contraseña
        return userRepository.save(user);
    }

    //Borrar un usuario
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
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

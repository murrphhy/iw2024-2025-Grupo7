package grupo7.services;

<<<<<<< HEAD
import grupo7.models.User;
import grupo7.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
=======
import grupo7.models.AppUser;
import grupo7.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class UserService implements UserDetailsService {
>>>>>>> main

    @Autowired
    private UserRepository userRepository;

<<<<<<< HEAD
    // Crear un nuevo usuario
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Leer todos los usuarios
    public List<User> getUsers() {
        return userRepository.findAll();
    }
    
    // Leer un usuario por ID
    public Optional<User> getUserByID(Long id) {
        return userRepository.findById(id);
    }
}
=======
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }

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
>>>>>>> main

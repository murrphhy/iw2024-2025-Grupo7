package grupo7.services;

import grupo7.models.User;
import grupo7.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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

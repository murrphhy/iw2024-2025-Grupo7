package grupo7.controllers;

import grupo7.models.User;
import grupo7.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    // Crear un nuevo usuario
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User usuario) {
        return userService.createUser(usuario);
    }

    // Leer todos los usuarios
    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    // Leer un usuario por ID
    @GetMapping("/{id}")
    public Optional<User> getUserByID(@PathVariable Long id) {
        return userService.getUserByID(id);
    }
    
}

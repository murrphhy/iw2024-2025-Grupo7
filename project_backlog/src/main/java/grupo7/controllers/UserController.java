package grupo7.controllers;

<<<<<<< HEAD
import grupo7.models.User;
import grupo7.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
=======
import com.vaadin.flow.server.auth.AnonymousAllowed;
import grupo7.models.AppUser;
import grupo7.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AnonymousAllowed
@RestController
>>>>>>> main
public class UserController {

    @Autowired
    private UserService userService;

<<<<<<< HEAD
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
    
=======
    @GetMapping("/api/users")
    public List<AppUser> getAllUsers() {
        System.out.println("Listando usuarios a través del endpoint");
        return userService.getAllUsers();
    }
>>>>>>> main
}

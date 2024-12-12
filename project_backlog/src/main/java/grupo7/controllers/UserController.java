package grupo7.controllers;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import grupo7.models.AppUser;
import grupo7.services.UserService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@AnonymousAllowed
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // Crear un nuevo usuario
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppUser createUser(@RequestBody AppUser usuario) {
        return userService.createUser(usuario);
    }

    // Leer todos los usuarios
    @GetMapping("/api/users")
    public List<AppUser> getAllUsers() {
        System.out.println("Listando usuarios a través del endpoint");
        return userService.getAllUsers();
    }

    // Leer un usuario por ID
    @GetMapping("/{id}")
    public Optional<AppUser> getUserByID(@PathVariable Long id) {
        return userService.getUserByID(id);
    }
}

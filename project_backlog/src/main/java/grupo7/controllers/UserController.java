package grupo7.controllers;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import grupo7.models.AppUser;
import grupo7.services.UserService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AnonymousAllowed
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Crear un nuevo usuario
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppUser createUser(@RequestBody AppUser usuario) {
        return userService.saveUser(usuario);
    }

    // Leer todos los usuarios
    @GetMapping
    public List<AppUser> getAllUsers() {
        System.out.println("Listando usuarios a través del endpoint");
        return userService.getAllUsers();
    }

    // Leer un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<AppUser> getUserById(@PathVariable Long id) {
        Optional<AppUser> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Actualizar un usuario por ID
    @PutMapping("/{id}")
    public ResponseEntity<AppUser> updateUser(@PathVariable Long id, @RequestBody AppUser userDetails) {
        Optional<AppUser> userOptional = userService.getUserById(id);

        if (userOptional.isPresent()) {
            AppUser user = userOptional.get();
            user.setUsername(userDetails.getUsername());
            user.setAcademicPosition(userDetails.getAcademicPosition());
            user.setCenter(userDetails.getCenter());
            user.setEmail(userDetails.getEmail());
            user.setisAdmin(userDetails.getIsAdmin());
            user.setPassword(userDetails.getPassword());
            user.setTechnicalArea(userDetails.getTechnicalArea());
            return ResponseEntity.ok(userService.saveUser(user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

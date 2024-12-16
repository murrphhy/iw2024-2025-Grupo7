package grupo7.controllers;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import grupo7.models.Support;
import grupo7.models.keys.SupportId;
import grupo7.services.SupportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AnonymousAllowed
@RestController
@RequestMapping("/api/supports")
public class SupportController {

    @Autowired
    private SupportService supportService;

    // Crear o actualizar una relación Support
    @PostMapping
    public ResponseEntity<Support> createOrUpdateSupport(@RequestBody Support support) {
        Support savedSupport = supportService.saveSupport(support);
        return ResponseEntity.ok(savedSupport);
    }

    // Obtener todas las relaciones Support
    @GetMapping
    public List<Support> getAllSupports() {
        System.out.println("Listando Supports a través del endpoint");
        return supportService.getAllSupports();
    }

    // Obtener una relación Support por ID combinado
    @GetMapping("/{userId}/{projectId}")
    public ResponseEntity<Support> getSupportById(
            @PathVariable Long userId,
            @PathVariable Long projectId) {
        SupportId supportId = new SupportId(userId, projectId);
        Optional<Support> support = supportService.getSupportById(supportId);

        return support.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar una relación Support por ID combinado
    @DeleteMapping("/{userId}/{projectId}")
    public ResponseEntity<Void> deleteSupport(
            @PathVariable Long userId,
            @PathVariable Long projectId) {
        SupportId supportId = new SupportId(userId, projectId);
        try {
            supportService.deleteSupport(supportId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

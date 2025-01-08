package grupo7.controllers;

import grupo7.models.TechnicianProject;
import grupo7.models.keys.TechnicianProjectId;
import grupo7.services.TechnicianProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/technician-project")
public class TechnicianProjectController {

    @Autowired
    private TechnicianProjectService technicianProjectService;

    // Obtener todos los TechnicianProject
    @GetMapping
    public List<TechnicianProject> getAllTechnicianProjects() {
        return technicianProjectService.getAllTechnicianProjects();
    }

    // Obtener un TechnicianProject por ID
    @GetMapping("/read/{userId}/{projectId}")
    public ResponseEntity<TechnicianProject> getTechnicianProjectById(
            @PathVariable Long userId,
            @PathVariable Long projectId) {
        TechnicianProjectId TechId = new TechnicianProjectId(userId, projectId);
        Optional<TechnicianProject> technicianProject = technicianProjectService.getTechnicianProjectById(TechId);
        
        return technicianProject.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear o actualizar un TechnicianProject
    @PostMapping("/create/{userId}/{projectId}")
    public ResponseEntity<TechnicianProject> createOrUpdateTechnicianProject(@RequestBody TechnicianProject technicianProject) {
        TechnicianProject savedTechnicianProject = technicianProjectService.saveTechnicianProject(technicianProject);
        return ResponseEntity.ok(savedTechnicianProject);
    }
    
    // Eliminar un TechnicianProject por ID
    @DeleteMapping("/delete/{userId}/{projectId}")
    public ResponseEntity<Void> deleteTechnicianProject(
            @PathVariable Long userId, 
            @PathVariable Long projectId) {
        TechnicianProjectId TechId = new TechnicianProjectId(userId, projectId);
        try {
            technicianProjectService.deleteTechnicianProject(TechId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

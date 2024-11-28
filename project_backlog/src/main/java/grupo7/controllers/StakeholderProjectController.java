package grupo7.controllers;

import grupo7.models.StakeholderProject;
import grupo7.models.keys.StakeholderProjectId;
import grupo7.services.StakeholderProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stakeholder-projects")
public class StakeholderProjectController {

    @Autowired
    private StakeholderProjectService stakeholderProjectService;

    // Obtener todos los StakeholderProject
    @GetMapping
    public List<StakeholderProject> getAllStakeholderProjects() {
        return stakeholderProjectService.getAllStakeholderProjects();
    }

    // Obtener un StakeholderProject por ID
    @GetMapping("/{userId}/{projectId}")
    public ResponseEntity<StakeholderProject> getStakeholderProjectById(
            @PathVariable Long userId,
            @PathVariable Long projectId) {
        StakeholderProjectId id = new StakeholderProjectId(userId, projectId);
        Optional<StakeholderProject> stakeholderProject = stakeholderProjectService.getStakeholderProjectById(id);

        return stakeholderProject.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear o actualizar un StakeholderProject
    @PostMapping
    public StakeholderProject createOrUpdateStakeholderProject(@RequestBody StakeholderProject stakeholderProject) {
        return stakeholderProjectService.saveStakeholderProject(stakeholderProject);
    }

    // Eliminar un StakeholderProject por ID
    @DeleteMapping("/{userId}/{projectId}")
    public ResponseEntity<Void> deleteStakeholderProject(
            @PathVariable Long userId,
            @PathVariable Long projectId) {
        StakeholderProjectId id = new StakeholderProjectId(userId, projectId);
        stakeholderProjectService.deleteStakeholderProject(id);
        return ResponseEntity.noContent().build();
    }
}

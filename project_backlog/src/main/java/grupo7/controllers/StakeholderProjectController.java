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
@RequestMapping("/api/stakeholder-project")
public class StakeholderProjectController {

    @Autowired
    private StakeholderProjectService stakeholderProjectService;

    // Obtener todos los StakeholderProject
    @GetMapping
    public List<StakeholderProject> getAllStakeholderProjects() {
        return stakeholderProjectService.getAllStakeholderProjects();
    }

    // Obtener un StakeholderProject por ID
    @GetMapping("/read/{userId}/{projectId}")
    public ResponseEntity<StakeholderProject> getStakeholderProjectById(
            @PathVariable Long userId,
            @PathVariable Long projectId) {
        StakeholderProjectId StakeId = new StakeholderProjectId(userId, projectId);
        Optional<StakeholderProject> stakeholderProject = stakeholderProjectService.getStakeholderProjectById(StakeId);

        return stakeholderProject.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear o actualizar un StakeholderProject
    @PostMapping("/create/{userId}/{projectId}")
    public ResponseEntity<StakeholderProject> createOrUpdateStakeholderProject(@RequestBody StakeholderProject stakeholderProject) {
        StakeholderProject savedStakeholderProject = stakeholderProjectService.saveStakeholderProject(stakeholderProject);
        return ResponseEntity.ok(savedStakeholderProject);
    }

    // Eliminar un StakeholderProject por ID
    @DeleteMapping("/delete/{userId}/{projectId}")
    public ResponseEntity<Void> deleteStakeholderProject(
            @PathVariable Long userId,
            @PathVariable Long projectId) {
        StakeholderProjectId StakeId = new StakeholderProjectId(userId, projectId);
        try {
            stakeholderProjectService.deleteStakeholderProject(StakeId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

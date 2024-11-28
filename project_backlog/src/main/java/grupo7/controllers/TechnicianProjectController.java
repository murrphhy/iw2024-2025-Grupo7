package grupo7.controllers;

import grupo7.models.keys.TechnicianProjectId;
import grupo7.models.Technician_Project;
import grupo7.services.TechnicianProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/technician_project")
public class TechnicianProjectController {

    @Autowired
    private TechnicianProjectService technicianProjectService;

    @PostMapping
    public Technician_Project createTechnicianProject(@RequestBody Technician_Project technicianProject) {
        return technicianProjectService.saveTechnicianProject(technicianProject);
    }

    @GetMapping("/{userId}/{projectId}")
    public Technician_Project getTechnicianProject(@PathVariable Long userId, @PathVariable Long projectId) {
        TechnicianProjectId id = new TechnicianProjectId();
        id.setUserId(userId);
        id.setProjectId(projectId);
        return technicianProjectService.getTechnicianProject(id);
    }

    @DeleteMapping("/{userId}/{projectId}")
    public void deleteTechnicianProject(@PathVariable Long userId, @PathVariable Long projectId) {
        TechnicianProjectId id = new TechnicianProjectId();
        id.setUserId(userId);
        id.setProjectId(projectId);
        technicianProjectService.deleteTechnicianProject(id);
    }
}

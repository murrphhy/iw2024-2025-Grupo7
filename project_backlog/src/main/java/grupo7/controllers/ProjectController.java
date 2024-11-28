package grupo7.controllers;

import grupo7.models.Project;
import grupo7.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Optional<Project> project = projectService.getProjectById(id);
        return project.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return projectService.saveProject(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        Optional<Project> projectOptional = projectService.getProjectById(id);

        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            project.setApplicantId(projectDetails.getApplicantId());
            project.setPromoterId(projectDetails.getPromoterId());
            project.setTitle(projectDetails.getTitle());
            project.setShortTitle(projectDetails.getShortTitle());
            project.setMemory(projectDetails.getMemory());
            project.setState(projectDetails.getState());
            project.setScope(projectDetails.getScope());
            project.setStartDate(projectDetails.getStartDate());
            project.setProjectRegulations(projectDetails.getProjectRegulations());
            project.setTechnicalSpecifications(projectDetails.getTechnicalSpecifications());
            return ResponseEntity.ok(projectService.saveProject(project));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        if (projectService.getProjectById(id).isPresent()) {
            projectService.deleteProject(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

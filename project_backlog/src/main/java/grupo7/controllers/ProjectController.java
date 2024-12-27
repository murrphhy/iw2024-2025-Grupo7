package grupo7.controllers;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import grupo7.models.Project;
import grupo7.services.ProjectService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AnonymousAllowed
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // Crear un nuevo proyecto
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project createProject(@RequestBody Project project) {
        return projectService.saveProject(project);
    }

    // Leer todos los proyectos
    @GetMapping
    public List<Project> getAllProjects() {
        System.out.println("Listando proyectos a través del endpoint");
        return projectService.getAllProjects();
    }

    // Leer un proyecto por ID
    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectId(@PathVariable Long projectId) {
        Optional<Project> project = projectService.getProjectById(projectId);
        return project.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Project> getProjectByTitle(@PathVariable String title) {
        Optional<Project> project = projectService.getProjectByTitle(title);
        return project.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Actualizar un proyecto por ID
    @PutMapping("/{projectId}")
    public ResponseEntity<Project> updateProject(@PathVariable Long projectId, @RequestBody Project projectDetails) {
        Optional<Project> projectOptional = projectService.getProjectById(projectId);

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

    // Borrar un proyecto por ID
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        if (projectService.getProjectById(projectId).isPresent()) {
            projectService.deleteProject(projectId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

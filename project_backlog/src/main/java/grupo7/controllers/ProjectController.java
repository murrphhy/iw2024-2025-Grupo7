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

import java.util.Map;
import java.util.HashMap;


/**
 * REST controller for managing projects.
 * Provides endpoints to create, read, update, and delete projects.
 */
@AnonymousAllowed
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;


    /**
     * Creates a new project.
     *
     * @param project The project to be created.
     * @return The created project.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project createProject(@RequestBody Project project) {
        return projectService.saveProject(project);
    }

    /**
     * Retrieves a list of all projects.
     *
     * @return A list of all projects.
     */
    @GetMapping
    public List<Project> getAllProjects() {
        System.out.println("Listando proyectos a través del endpoint");
        return projectService.getAllProjects();
    }

    /**
     * Retrieves a specific project by its ID.
     *
     * @param projectId The ID of the project to retrieve.
     * @return A {@link ResponseEntity} containing the project if found, or a 404 Not Found status if not.
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectId(@PathVariable Long projectId) {
        Optional<Project> project = projectService.getProjectById(projectId);
        return project.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves a specific project by its title.
     *
     * @param title The title of the project to retrieve.
     * @return A {@link ResponseEntity} containing the project if found, or a 404 Not Found status if not.
     */
    @GetMapping("/title/{title}")
    public ResponseEntity<Project> getProjectByTitle(@PathVariable String title) {
        Optional<Project> project = projectService.getProjectByTitle(title);
        return project.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates a specific project by its ID.
     *
     * @param projectId      The ID of the project to update.
     * @param projectDetails The details to update the project with.
     * @return A {@link ResponseEntity} containing the updated project if successful, or a 404 Not Found status if the project does not exist.
     */
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


    /**
     * Deletes a specific project by its ID.
     *
     * @param projectId The ID of the project to delete.
     * @return A {@link ResponseEntity} with a 204 No Content status if successful, or a 404 Not Found status if the project does not exist.
     */
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        if (projectService.getProjectById(projectId).isPresent()) {
            projectService.deleteProject(projectId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Obtener proyectos con nota del CIO.
    @GetMapping("/{projectId}/details")
    public ResponseEntity<Map<String, Object>> getProjectDetails(@PathVariable Long projectId) {
        Optional<Project> project = projectService.getProjectById(projectId);

        if (project.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("project", project.get());
            response.put("cioRating", projectService.getCioRating(projectId)); // Método que devuelve la nota del CIO.
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

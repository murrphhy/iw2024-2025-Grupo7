package grupo7.services;


import grupo7.models.TechnicianProject;
import grupo7.models.keys.TechnicianProjectId;
import grupo7.repositories.TechnicianProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import grupo7.models.Project;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TechnicianProjectService {

    @Autowired
    private TechnicianProjectRepository technicianProjectRepository;

    @Autowired
    private ProjectService projectService;

    public List<TechnicianProject> getAllTechnicianProjects() {
        return technicianProjectRepository.findAll();
    }

    public Optional<TechnicianProject> getTechnicianProjectById(TechnicianProjectId TechId) {
        return technicianProjectRepository.findById(TechId);
    }

    public TechnicianProject saveTechnicianProject(TechnicianProject technicianProject) {
        return technicianProjectRepository.save(technicianProject);
    }

    // Eliminar un TechnicianProject por su ID compuesto
    public void deleteTechnicianProject(TechnicianProjectId TechId) {
        if (technicianProjectRepository.existsById(TechId)) {
            technicianProjectRepository.deleteById(TechId);
        } else {
            throw new IllegalArgumentException("El Technician con el ID.");
        }
    }

    public void saveTechnicalRating(Long userId, Long projectId, Double rating, int humanResources, BigDecimal financialResources, String technicalResources) {
        // Guardar la nota en TechnicianProject
        TechnicianProjectId id = new TechnicianProjectId(userId, projectId);
        TechnicianProject technicianProject = technicianProjectRepository.findById(id)
                .orElse(new TechnicianProject(userId, projectId, rating,humanResources, financialResources, technicalResources));
        technicianProject.setProjectAppraisal(rating);
        technicianProjectRepository.save(technicianProject);

        // Actualizar el campo technical_suitability y estado en la tabla Project
        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el proyecto con ID: " + projectId));

        project.setTechnicalSuitability(rating);
        project.setState("evaluado");
        projectService.saveProject(project); // Asegúrate de que este método esté implementado en ProjectService
    }
}
package grupo7.services;

import grupo7.models.Project;
import grupo7.repositories.ProjectRepository;
import grupo7.repositories.TechnicianProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TechnicianProjectRepository technicianProjectRepository;

    // Leer todos los proyectos
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }


    public Optional<Project> getProjectById(Long projectId) {
        return projectRepository.findById(projectId);
    }

    public Optional<Project> getProjectByTitle(String title) {
        return projectRepository.findByTitle(title);
    }

    // Crear y guardar un proyectos
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    //Borrar un proyecto
    public void deleteProject(Long projectId) {
        projectRepository.deleteById(projectId);
    }


}

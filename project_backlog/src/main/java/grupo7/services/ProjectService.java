package grupo7.services;

import grupo7.models.Project;
import grupo7.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    // Leer todos los proyectos
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // Leer un proyectos por ID
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    // Crear y guardar un proyectos
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    //Borrar un proyecto
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}

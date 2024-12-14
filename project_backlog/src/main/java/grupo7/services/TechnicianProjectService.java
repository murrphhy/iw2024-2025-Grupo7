package grupo7.services;

import grupo7.models.TechnicianProject;
import grupo7.models.keys.TechnicianProjectId;
import grupo7.repositories.TechnicianProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TechnicianProjectService {

    @Autowired
    private TechnicianProjectRepository technicianProjectRepository;

    // Obtener todos los TechnicianProject
    public List<TechnicianProject> getAllTechnicianProjects() {
        return technicianProjectRepository.findAll();
    }

    // Obtener un TechnicianProject por su ID compuesto
    public Optional <TechnicianProject> getTechnicianProjectById(TechnicianProjectId TechId) {
        return technicianProjectRepository.findById(TechId);
    }

    // Crear o actualizar un TechnicianProject
    public TechnicianProject saveTechnicianProject(TechnicianProject technicianProject) {
        return technicianProjectRepository.save(technicianProject);
    }

    // Eliminar un TechnicianProject por su ID compuesto
    public void deleteTechnicianProject(TechnicianProjectId TechId) {
        if (technicianProjectRepository.existsById(TechId)) {
            technicianProjectRepository.deleteById(TechId);
        } else {
            throw new IllegalArgumentException("El Stakeholder con el ID especificado no existe.");
        }
    }

}

package grupo7.services;

import grupo7.models.keys.TechnicianProjectId;

import grupo7.models.Technician_Project;
import grupo7.repositories.TechnicianProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TechnicianProjectService {

    @Autowired
    private TechnicianProjectRepository technicianProjectRepository;

    public Technician_Project saveTechnicianProject(Technician_Project technicianProject) {
        return technicianProjectRepository.save(technicianProject);
    }

    public Technician_Project getTechnicianProject(TechnicianProjectId id) {
        return technicianProjectRepository.findById(id).orElse(null);
    }

    public void deleteTechnicianProject(TechnicianProjectId id) {
        technicianProjectRepository.deleteById(id);
    }

    // Otros métodos según sea necesario
}

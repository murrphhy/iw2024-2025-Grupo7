package grupo7.services;

import grupo7.models.StakeholderProject;
import grupo7.models.keys.StakeholderProjectId;
import grupo7.repositories.StakeholderProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StakeholderProjectService {

    @Autowired
    private StakeholderProjectRepository stakeholderProjectRepository;

    // Obtener todos los StakeholderProject
    public List<StakeholderProject> getAllStakeholderProjects() {
        return stakeholderProjectRepository.findAll();
    }

    // Obtener un StakeholderProject por su ID compuesto
    public Optional<StakeholderProject> getStakeholderProjectById(StakeholderProjectId StakeId) {
        return stakeholderProjectRepository.findById(StakeId);
    }

    // Crear o actualizar un StakeholderProject
    public StakeholderProject saveStakeholderProject(StakeholderProject stakeholderProject) {
        return stakeholderProjectRepository.save(stakeholderProject);
    }

    // Eliminar un StakeholderProject por su ID compuesto
    public void deleteStakeholderProject(StakeholderProjectId StakeId) {
        if (stakeholderProjectRepository.existsById(StakeId)) {
            stakeholderProjectRepository.deleteById(StakeId);
        } else {
            throw new IllegalArgumentException("El Stakeholder con el ID especificado no existe.");
        }
    }
}

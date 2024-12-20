package grupo7.repositories;

import grupo7.models.StakeholderProject;
import grupo7.models.keys.StakeholderProjectId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StakeholderProjectRepository extends JpaRepository<StakeholderProject, StakeholderProjectId> {
    // Aquí puedes añadir consultas personalizadas si las necesitas
}


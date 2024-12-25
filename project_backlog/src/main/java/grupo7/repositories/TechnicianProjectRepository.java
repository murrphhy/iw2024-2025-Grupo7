package grupo7.repositories;

import grupo7.models.TechnicianProject;
import grupo7.models.keys.TechnicianProjectId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnicianProjectRepository extends JpaRepository<TechnicianProject, TechnicianProjectId> {
    // Puedes agregar métodos personalizados si es necesario
}

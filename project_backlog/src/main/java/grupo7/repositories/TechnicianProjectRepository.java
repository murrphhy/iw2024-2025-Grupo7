package grupo7.repositories;

import grupo7.models.Technician_Project;
import grupo7.models.keys.TechnicianProjectId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnicianProjectRepository extends JpaRepository<Technician_Project, TechnicianProjectId> {
    // Puedes agregar métodos personalizados si es necesario
}

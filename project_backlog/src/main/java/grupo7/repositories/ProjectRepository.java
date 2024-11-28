package grupo7.repositories;

import grupo7.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Consultas personalizadas si son necesarias
}

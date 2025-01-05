package grupo7.repositories;

import grupo7.models.TechnicianProject;
import grupo7.models.keys.TechnicianProjectId;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


@Repository
public interface TechnicianProjectRepository extends JpaRepository<TechnicianProject, TechnicianProjectId> {

}
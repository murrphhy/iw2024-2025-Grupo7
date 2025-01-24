package grupo7.repositories;

import grupo7.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAll();

    Optional<Project> findByTitle(String title);

    Optional<Project> findById(Long projectId);


    List<Project> findByCallId(Long callId);

}

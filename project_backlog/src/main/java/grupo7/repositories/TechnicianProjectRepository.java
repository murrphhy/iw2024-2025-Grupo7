package grupo7.repositories;

import grupo7.models.TechnicianProject;
import grupo7.models.keys.TechnicianProjectId;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianProjectRepository extends JpaRepository<TechnicianProject, TechnicianProjectId> {
    @Query("SELECT tp.projectAppraisal FROM TechnicianProject tp WHERE tp.project_id = :projectId")
    Optional<Integer> findCioRatingByProjectId(Long projectId);

    @Query("SELECT tp FROM TechnicianProject tp WHERE tp.projectAppraisal IS NOT NULL")
    List<TechnicianProject> findProjectsWithCioRating();
}

package grupo7.repositories;

import grupo7.models.Calls;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CallRepository extends JpaRepository<Calls, Long> {

    // Encontrar todas las convocatorias
    List<Calls> findAll();


    @Query("SELECT c FROM Calls c LEFT JOIN FETCH c.projects ORDER BY c.name DESC")
    List<Calls> findAllWithProjects();


    // Encontrar convocatorias por estado
    List<Calls> findByState(String state);

    // Buscar una convocatoria por nombre
    Optional<Calls> findByName(String name);

}

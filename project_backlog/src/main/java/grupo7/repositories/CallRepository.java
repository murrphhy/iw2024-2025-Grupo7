package grupo7.repositories;

import grupo7.models.Calls;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CallRepository extends JpaRepository<Calls, Long> {

    // Encontrar todas las convocatorias
    List<Calls> findAll();

    // Encontrar convocatorias por estado
    List<Calls> findByState(String state);

    // Buscar una convocatoria por nombre
    Optional<Calls> findByName(String name);

}

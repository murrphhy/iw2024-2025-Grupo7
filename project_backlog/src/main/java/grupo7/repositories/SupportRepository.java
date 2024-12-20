package grupo7.repositories;

import grupo7.models.Support;
import grupo7.models.keys.SupportId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportRepository extends JpaRepository<Support, SupportId> {
    // Aquí puedes agregar métodos personalizados si los necesitas
}


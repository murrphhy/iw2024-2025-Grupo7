package grupo7.repository;

import grupo7.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Aquí puede se pueden añadir consultas personalizadas como buscar por email
}

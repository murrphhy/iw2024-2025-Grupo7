package grupo7.repositories;

import grupo7.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, String> {

    List<AppUser> findAll();

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByUsername(String username);

}

package grupo7.repositories;

import com.vaadin.open.App;
import grupo7.models.AppUser;
import grupo7.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    List<AppUser> findAll();

    List<AppUser> findAllByRole(Role role);

    Optional<AppUser> findByEmail(String role);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByRole(Role role);

}

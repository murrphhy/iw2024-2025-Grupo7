package grupo7.seeders;

import grupo7.models.AppUser;
import grupo7.models.Role;
import grupo7.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DBSeeder {
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("user").isEmpty()) {
                AppUser user = new AppUser();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("password"));
                user.setEmail("user@email.com");
                user.setAcademicPosition("Profesor");
                user.setCenter("ESI");
                user.setTechnicalArea("Informatica");
                user.setRole(Role.valueOf("ADMINISTRATOR"));
                userRepository.save(user);
            }
        };
    }
}

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
            if (userRepository.findByUsername("adminUser").isEmpty()) {
                AppUser admin = new AppUser();
                admin.setUsername("adminUser");
                admin.setPassword(passwordEncoder.encode("password"));
                admin.setEmail("admin@uca.es");
                admin.setAcademicPosition("Administrador Principal");
                admin.setCenter("Centro de Administración");
                admin.setTechnicalArea("Gestión Empresarial");
                admin.setRole(Role.valueOf("ADMINISTRATOR"));
                userRepository.save(admin);
            }

            if (userRepository.findByUsername("applicantUser").isEmpty()) {
                AppUser applicant = new AppUser();
                applicant.setUsername("applicantUser");
                applicant.setPassword(passwordEncoder.encode("password"));
                applicant.setEmail("applicant@uca.es");
                applicant.setAcademicPosition("Solicitante");
                applicant.setCenter("Centro de Solicitantes");
                applicant.setTechnicalArea("Ingeniería");
                applicant.setRole(Role.valueOf("APPLICANT"));
                userRepository.save(applicant);
            }

            if (userRepository.findByUsername("technicalUser").isEmpty()) {
                AppUser technical = new AppUser();
                technical.setUsername("technicalUser");
                technical.setPassword(passwordEncoder.encode("password"));
                technical.setEmail("technical@uca.es");
                technical.setAcademicPosition("Soporte Técnico");
                technical.setCenter("Centro Técnico");
                technical.setTechnicalArea("Soporte Informático");
                technical.setRole(Role.valueOf("TECHNICAL"));
                userRepository.save(technical);
            }

            if (userRepository.findByUsername("cioUser").isEmpty()) {
                AppUser cio = new AppUser();
                cio.setUsername("cioUser");
                cio.setPassword(passwordEncoder.encode("password"));
                cio.setEmail("cio@uca.es");
                cio.setAcademicPosition("Chief Information Officer");
                cio.setCenter("Centro de Información");
                cio.setTechnicalArea("Tecnología de la Información");
                cio.setRole(Role.valueOf("CIO"));
                userRepository.save(cio);
            }
        };
    }
}

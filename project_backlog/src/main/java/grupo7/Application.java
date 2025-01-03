package grupo7;

import io.github.cdimascio.dotenv.Dotenv;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@Theme(value = "projectbacklog")
@SpringBootApplication(scanBasePackages = "grupo7")
@EnableCaching
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        // Loading environment variables...
        Dotenv dotenv = Dotenv.configure()
                .load();

        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        SpringApplication.run(Application.class, args);
    }
}

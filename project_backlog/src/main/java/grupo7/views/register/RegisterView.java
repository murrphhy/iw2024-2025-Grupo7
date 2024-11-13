package grupo7.views.register;

import grupo7.models.Applicant;
import grupo7.repositories.UserRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.Route;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route("register")
public class RegisterView extends VerticalLayout {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterView(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        TextField nameField = new TextField("Nombre");
        EmailField emailField = new EmailField("Correo Electrónico");
        PasswordField passwordField = new PasswordField("Contraseña");
        PasswordField confirmPasswordField = new PasswordField("Confirmar Contraseña");
        TextField unitField = new TextField("Unidad");

        Button registerButton = new Button("Registrarse", event -> {
            if (!passwordField.getValue().equals(confirmPasswordField.getValue())) {
                Notification.show("Las contraseñas no coinciden");
                return;
            }

            if (userRepository.findByEmail(emailField.getValue()).isPresent()) {
                Notification.show("El correo electrónico ya está registrado");
                return;
            }

            // Creación del Applicant
            String name = nameField.getValue();
            String email = emailField.getValue();
            String password = passwordEncoder.encode(passwordField.getValue());
            boolean isAdmin = false;
            String unit = unitField.getValue();

            Applicant applicant = new Applicant(name, email, password, isAdmin, unit);

            userRepository.save(applicant);

            Notification.show("Registro exitoso");
            getUI().ifPresent(ui -> ui.navigate("login"));
        });

        FormLayout formLayout = new FormLayout();
        formLayout.add(nameField, emailField, passwordField, confirmPasswordField, unitField, registerButton);

        // Reemplazo de Label por H2
        add(new H2("Registro de Usuario (Applicant)"), formLayout);
    }
}

package grupo7.views.adminregister;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import grupo7.models.*;
import grupo7.repositories.UserRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route("adminregister/register")
@Secured("ROLE_ADMIN")
public class AdminRegister extends VerticalLayout {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminRegister(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        TextField nameField = new TextField("Nombre");
        EmailField emailField = new EmailField("Correo Electrónico");
        PasswordField passwordField = new PasswordField("Contraseña");
        PasswordField confirmPasswordField = new PasswordField("Confirmar Contraseña");

        Select<String> userTypeSelect = new Select<>();
        userTypeSelect.setLabel("Tipo de Usuario");
        userTypeSelect.setItems("Technician", "Promoter", "CIO");
        userTypeSelect.setValue("Technician");

        // Campo específico
        TextField specificField = new TextField("Campo Específico");

        userTypeSelect.addValueChangeListener(event -> {
            String userType = event.getValue();
            switch (userType) {
                case "Technician":
                    specificField.setLabel("Área Técnica");
                    break;
                case "Promoter":
                    specificField.setLabel("Importancia");
                    break;
                case "CIO":
                    specificField.setLabel("Posición");
                    break;
            }
        });

        Button registerButton = new Button("Registrar Usuario", event -> {
            // Validaciones
            if (!passwordField.getValue().equals(confirmPasswordField.getValue())) {
                Notification.show("Las contraseñas no coinciden");
                return;
            }

            if (userRepository.findByEmail(emailField.getValue()).isPresent()) {
                Notification.show("El correo electrónico ya está registrado");
                return;
            }

            String name = nameField.getValue();
            String email = emailField.getValue();
            String password = passwordEncoder.encode(passwordField.getValue());
            boolean isAdmin = false;
            String specificValue = specificField.getValue();

            Users user;

            switch (userTypeSelect.getValue()) {
                case "Technician":
                    user = new Technician(name, email, password, isAdmin, specificValue);
                    break;
                case "Promoter":
                    int importance;
                    try {
                        importance = Integer.parseInt(specificValue);
                    } catch (NumberFormatException e) {
                        Notification.show("La importancia debe ser un número");
                        return;
                    }
                    user = new Promoter(name, email, password, isAdmin, importance);
                    break;
                case "CIO":
                    user = new Cio(name, email, password, isAdmin, specificValue);
                    break;
                default:
                    Notification.show("Tipo de usuario no válido");
                    return;
            }

            userRepository.save(user);

            Notification.show("Usuario registrado exitosamente");
            getUI().ifPresent(ui -> ui.navigate("admin/dashboard"));
        });

        FormLayout formLayout = new FormLayout();
        formLayout.add(nameField, emailField, passwordField, confirmPasswordField, userTypeSelect, specificField, registerButton);

        add(new H2("Registro de Usuarios (Administrador)"), formLayout);
    }
}

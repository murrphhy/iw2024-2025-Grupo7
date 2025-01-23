package grupo7.views.Register;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import grupo7.models.AppUser;
import grupo7.models.Role;
import grupo7.services.UserService;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.component.combobox.ComboBox;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@PageTitle("Registro de Usuario")
@Route("register")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    private final UserService userService;
    private final Binder<AppUser> binder = new Binder<>(AppUser.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public RegisterView(UserService userService) {
        this.userService = userService;

        Div registrationForm = createRegistrationForm();
        registrationForm.getStyle().set("margin-top", "8rem");
        registrationForm.getStyle().set("margin-bottom", "2rem");
        registrationForm.getStyle().set("display", "flex");
        registrationForm.getStyle().set("align-items", "center");

        Image logo = new Image("https://gabcomunicacion.uca.es/wp-content/uploads/2017/05/Logo-V2-Color-Imprsi%C3%B3n-100x133-mm-jpg.jpg?u", "Logo UCA");
        logo.setWidth("300px");
        logo.getStyle().set("margin-bottom", "2rem");
        logo.getStyle().set("margin-top", "4rem");

        add(logo, registrationForm);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();
        getStyle().set("text-align", "center");
    }

    private Div createRegistrationForm() {
        Div formLayoutContainer = new Div();

        FormLayout registrationForm = new FormLayout();
        registrationForm.setWidth("50%");

        TextField usernameField = new TextField("Nombre de usuario");
        TextField emailField = new TextField("Correo Electrónico");
        PasswordField passwordField = new PasswordField("Contraseña");
        ComboBox<String> positionField = new ComboBox<>("Posición Académica");
        positionField.setItems("Decano", "Rector", "Técnico", "Vicerector", "Profesor");
        TextField areaField = new TextField("Área Técnica");
        TextField centerField = new TextField("Centro");

        binder.forField(usernameField)
                .asRequired("El nombre de usuario es obligatorio.")
                .bind(AppUser::getUsername, AppUser::setUsername);

        binder.forField(emailField)
                .asRequired("El correo electrónico es obligatorio.")
                .bind(AppUser::getEmail, AppUser::setEmail);

        binder.forField(passwordField)
                .withValidator(pass -> pass.length() >= 6,
                        "La contraseña debe tener al menos 6 caracteres.")
                .bind(
                        user -> "",
                        (user, newPassword) -> {
                            if (!newPassword.isEmpty()) {
                                user.setPassword(newPassword);
                            }
                        }
                );

        binder.forField(positionField)
                .asRequired("La posición académica es obligatoria.")
                .bind(AppUser::getAcademicPosition, AppUser::setAcademicPosition);

        binder.forField(areaField)
                .bind(AppUser::getTechnicalArea, AppUser::setTechnicalArea);

        binder.forField(centerField)
                .asRequired("El centro es obligatorio.")
                .bind(AppUser::getCenter, AppUser::setCenter);

        Button registerButton = new Button("Registrarse", event -> registerUser());
        Button resetButton = new Button("Limpiar", event -> binder.readBean(null));

        registerButton.addThemeName("primary");

        HorizontalLayout buttonLayout = new HorizontalLayout(registerButton, resetButton);
        buttonLayout.getStyle().set("margin-top", "1rem");

        registrationForm.add(
                usernameField, emailField,
                passwordField, positionField,
                areaField, centerField,
                buttonLayout
        );

        registrationForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),      // 1 columna para pantallas pequeñas
                new FormLayout.ResponsiveStep("600px", 2)   // 2 columnas para pantallas >= 600px
        );


        registrationForm.setColspan(buttonLayout, 2);

        formLayoutContainer.getStyle().set("display", "flex");
        formLayoutContainer.getStyle().set("justify-content", "center");
        formLayoutContainer.getStyle().set("align-items", "center");

        formLayoutContainer.add(registrationForm);
        return formLayoutContainer;
    }


    private void registerUser() {
        AppUser newUser = new AppUser();
        try {
            binder.writeBean(newUser);
            newUser.setRole(Role.APPLICANT);
            userService.saveUser(newUser);
            Notification notification = Notification.show("Usuario registrado correctamente.", 3000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            binder.readBean(null);

            // Redirigir al login después de un retraso
            notification.addDetachListener(event -> getUI().ifPresent(ui -> ui.navigate("login")));
        } catch (ValidationException e) {
            Notification.show("Error en la validación de los datos.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}

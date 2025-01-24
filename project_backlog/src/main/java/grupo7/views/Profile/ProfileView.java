package grupo7.views.Profile;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import grupo7.models.AppUser;
import grupo7.services.UserService;
import grupo7.security.AuthenticatedUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@PageTitle("Perfil de Usuario")
@Route("profile")
@Menu(order = 5)
@PermitAll
public class ProfileView extends VerticalLayout {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Binder<AppUser> binder = new Binder<>(AppUser.class);

    private final TextField usernameField = new TextField("Nombre de usuario");
    private final TextField emailField = new TextField("Correo Electrónico");

    private final PasswordField currentPasswordField = new PasswordField("Contraseña Actual");

    private final PasswordField newPasswordField = new PasswordField("Nueva Contraseña");

    private final ComboBox<String> positionField = new ComboBox<>("Posición Académica");
    private final TextField areaField = new TextField("Área Técnica");
    private final TextField centerField = new TextField("Centro");

    // Usuario actual
    private AppUser currentUser;

    public ProfileView(UserService userService, AuthenticatedUser authenticatedUser) {
        this.userService = userService;

        authenticatedUser.get().ifPresentOrElse(
                user -> this.currentUser = user,
                () -> {
                    Notification.show("No hay un usuario autenticado.", 3000, Notification.Position.MIDDLE);
                    getUI().ifPresent(ui -> ui.navigate("login"));
                }
        );

        if (currentUser == null) {
            return;
        }

        add(new H2("Perfil de Usuario"));
        add(createProfileForm());
        setAlignItems(Alignment.CENTER);
        setSizeFull();

        configureBinder();
        binder.readBean(currentUser);
    }

    private Div createProfileForm() {
        Div formLayoutContainer = new Div();
        FormLayout formLayout = new FormLayout();

        positionField.setItems("Decano", "Rector", "Técnico", "Vicerector", "Profesor");

        Button updateButton = new Button("Actualizar datos", event -> confirmUpdate());
        updateButton.addThemeName("primary");

        Button deleteButton = new Button("Borrar cuenta", event -> confirmDelete());
        deleteButton.getStyle().set("color", "red");

        HorizontalLayout buttonsLayout = new HorizontalLayout(updateButton, deleteButton);

        formLayout.add(
                usernameField, emailField,
                currentPasswordField, newPasswordField,
                positionField, areaField,
                centerField, buttonsLayout
        );

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );
        formLayout.setColspan(buttonsLayout, 2);

        formLayoutContainer.add(formLayout);
        formLayoutContainer.getStyle().set("display", "flex");
        formLayoutContainer.getStyle().set("justify-content", "center");

        return formLayoutContainer;
    }

    private void configureBinder() {
        binder.forField(usernameField)
                .asRequired("El nombre de usuario es obligatorio.")
                .bind(AppUser::getUsername, AppUser::setUsername);

        binder.forField(emailField)
                .asRequired("El correo electrónico es obligatorio.")
                .bind(AppUser::getEmail, AppUser::setEmail);

        binder.forField(currentPasswordField)
                .withValidator(
                        currentPass -> {
                            if (newPasswordField.getValue().isEmpty()) {
                                return true;
                            }
                            return passwordEncoder.matches(currentPass, currentUser.getPassword());
                        },
                        "La contraseña actual no coincide"
                )
                .bind(user -> "", (user, currentPass) -> {});

        binder.forField(newPasswordField)
                .withValidator(pass -> pass.isEmpty() || pass.length() >= 6,
                        "La nueva contraseña debe tener al menos 6 caracteres (o dejar vacío para no cambiar)")
                .bind(
                        user -> "",
                        (user, newPassword) -> {
                            if (newPassword != null && !newPassword.isEmpty()) {
                                user.setPassword(passwordEncoder.encode(newPassword));
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
    }

    private void confirmUpdate() {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Confirmar edición");
        confirmDialog.setText("¿Estás seguro de que deseas actualizar tus datos?");
        confirmDialog.setCancelable(true);
        confirmDialog.setRejectable(true);
        confirmDialog.setRejectText("Cancelar");
        confirmDialog.setConfirmText("Actualizar");
        confirmDialog.addConfirmListener(event -> updateProfile());
        confirmDialog.open();
    }


    private void updateProfile() {
        try {
            binder.writeBean(currentUser);
            userService.saveUser(currentUser);
            Notification.show("Datos actualizados correctamente.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
            Notification.show("Error de validación de los datos: " + e.getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }


    private void confirmDelete() {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("¿Borrar cuenta?");
        confirmDialog.setText("Esta acción no se puede deshacer. ¿Estás seguro de que deseas eliminar tu cuenta?");
        confirmDialog.setCancelable(true);
        confirmDialog.setRejectable(true);
        confirmDialog.setRejectText("Cancelar");
        confirmDialog.setConfirmText("Borrar");
        confirmDialog.addConfirmListener(event -> deleteAccount());
        confirmDialog.open();
    }

    private void deleteAccount() {
        userService.deleteUser(currentUser.getId());
        Notification.show("Cuenta eliminada correctamente.", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        getUI().ifPresent(ui -> ui.navigate("login"));
    }
}

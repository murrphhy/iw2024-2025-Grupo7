package grupo7.views.adminpanel;

import ch.qos.logback.classic.encoder.JsonEncoder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import grupo7.models.AppUser;
import grupo7.models.Role;
import grupo7.repositories.UserRepository;
import grupo7.services.UserService;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.component.formlayout.FormLayout;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@PageTitle("Admin Panel")
@Route("/admin-panel")
@Menu(order = 2)
@RolesAllowed("ADMINISTRATOR")
public class AdminPanel extends Div {

    private final Grid<AppUser> userGrid = new Grid<>(AppUser.class, false);
    private final Binder<AppUser> binder = new Binder<>(AppUser.class);
    private AppUser currentUser;

    private final UserService userService; // Instead of userRepository
    private FormLayout formLayout;
    private PasswordField passwordField; // Declaración como variable de instancia


    public AdminPanel(UserService userService) {
        this.userService = userService;

        HorizontalLayout toolbar = createToolbar();

        configureGrid();

        Div formLayout = createForm();

        add(toolbar, userGrid, formLayout);

        updateGrid();
    }

    private void configureGrid() {
        userGrid.addColumn(AppUser::getUsername).setHeader("Nombre");
        userGrid.addColumn(AppUser::getEmail).setHeader("Correo Electrónico");
        userGrid.addColumn(AppUser::getAcademicPosition).setHeader("Posición Académica");
        userGrid.addColumn(AppUser::getCenter).setHeader("Centro");
        userGrid.addColumn(AppUser::getTechnicalArea).setHeader("Área Técnica");
        userGrid.addColumn(AppUser::getRole).setHeader("Rol");

        userGrid.addComponentColumn(user -> {
            Button editButton = new Button("Editar", e -> editUser(user));
            Button deleteButton = new Button("Eliminar", e -> deleteUser(user));
            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            return actions;
        }).setHeader("Acciones");

        userGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }

    private Div createForm() {
        Div formLayoutContainer = new Div();

        // Formulario principal
        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("70%");

        // Configuración de campos
        TextField usernameField = new TextField("Username");
        TextField emailField = new TextField("Correo Electrónico");
        passwordField = new PasswordField("Contraseña");
        ComboBox<String> positionField = new ComboBox<>("Posición Académica");
        positionField.setItems("Decano", "Rector", "Técnico", "Vicerector", "Profesor");
        TextField areaField = new TextField("Área Técnica");
        TextField centerField = new TextField("Centro");
        ComboBox<Role> roleField = new ComboBox<>("Rol");
        roleField.setItems(Role.values()); // Enum.values() obtiene todas las constantes del enum
        roleField.setItemLabelGenerator(Role::name);
        binder.forField(usernameField)
                .asRequired("El nombre de usuario es obligatorio")
                .bind(AppUser::getUsername, AppUser::setUsername);

        binder.forField(emailField)
                .asRequired("El correo electrónico es obligatorio")
                .bind(AppUser::getEmail, AppUser::setEmail);

        binder.forField(passwordField)
                .withValidator(pass -> pass.length() >= 6 || pass.isEmpty(),
                        "La contraseña debe tener al menos 6 caracteres")
                .bind(
                        user -> "",

                        (user, newPassword) -> {
                            if (!newPassword.isEmpty()) {
                                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                                user.setPassword(passwordEncoder.encode(newPassword));
                            }
                        }
                );

        binder.forField(positionField)
                .asRequired("La posición académica es obligatoria")
                .bind(AppUser::getAcademicPosition, AppUser::setAcademicPosition);

        binder.forField(areaField)
                .bind(AppUser::getTechnicalArea, AppUser::setTechnicalArea);

        binder.forField(centerField)
                .asRequired("El centro es obligatorio")
                .bind(AppUser::getCenter, AppUser::setCenter);

        binder.forField(roleField)
                .bind(AppUser::getRole, AppUser::setRole);




        Button saveButton = new Button("Guardar", e -> saveUser());
        Button cancelButton = new Button("Cancelar", e -> cancelEdit());
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);

        formLayout.add(
                usernameField, emailField,
                passwordField, positionField,
                areaField, centerField,
                roleField, buttonLayout
        );
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        formLayout.setColspan(buttonLayout, 2);

        formLayout.setVisible(false);

        formLayoutContainer.add(formLayout);
        formLayout.getStyle().set("margin-left", "50px");

        this.formLayout = formLayout;

        return formLayoutContainer;
    }

    private HorizontalLayout createToolbar() {
        Button addNewUserButton = new Button("Nuevo Usuario", e -> addNewUser());

        return new HorizontalLayout(addNewUserButton);
    }



    private void updateGrid() {
        userGrid.setItems(userService.getAllUsers());
    }

    private void addNewUser() {
        currentUser = new AppUser();
        currentUser.setAcademicPosition("Profesor");
        currentUser.setCenter("Centro predeterminado");
        binder.readBean(currentUser);
        formLayout.setVisible(true);
    }


    private void editUser(AppUser user) {
        if (user != null) {
            currentUser = user;

            // Leer datos en el formulario
            binder.readBean(user);

            // Ocultar el campo de contraseña
            passwordField.setVisible(false);

            // Hacer visible el formulario
            formLayout.setVisible(true);

            System.out.println("Editando usuario: " + user);
        } else {
            System.out.println("Usuario nulo seleccionado");
        }
    }


    private void saveUser() {
        try {
            if (currentUser != null) {
                binder.writeBean(currentUser);
                userService.saveUser(currentUser);
                currentUser = null;
                updateGrid();
                formLayout.setVisible(false);
            }
        } catch (ValidationException e) {
            e.printStackTrace();
            Notification.show("Por favor, revisa los campos del formulario");
        }
        passwordField.setVisible(true);

    }



    private void deleteUser(AppUser user) {
        userService.deleteUser(user.getId());
        updateGrid();
    }

    private void cancelEdit() {
        currentUser = null;
        passwordField.setVisible(true);

        binder.readBean(null);

        formLayout.setVisible(false);
    }
}

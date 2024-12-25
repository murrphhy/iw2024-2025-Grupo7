package grupo7.views.adminpanel;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import grupo7.models.AppUser;
import grupo7.repositories.UserRepository;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.component.formlayout.FormLayout;

import java.util.List;

@PageTitle("Admin Panel")
@Route(value = "admin-panel")
@Menu(order = 3)
@PermitAll
public class AdminPanel extends Div {

    private final Grid<AppUser> userGrid = new Grid<>(AppUser.class, false);
    private final Binder<AppUser> binder = new Binder<>(AppUser.class);
    private AppUser currentUser;

    private final UserRepository userRepository;

    public AdminPanel(UserRepository userRepository) {
        this.userRepository = userRepository;

        HorizontalLayout toolbar = createToolbar();

        configureGrid();

        Div formLayout = createForm();

        add(toolbar, userGrid, formLayout);

        updateGrid();
    }

    private HorizontalLayout createToolbar() {
        Button addNewUserButton = new Button("Nuevo Usuario", e -> addNewUser());

        return new HorizontalLayout(addNewUserButton);
    }

    private void configureGrid() {
        userGrid.addColumn(AppUser::getUsername).setHeader("Nombre");
        userGrid.addColumn(AppUser::getEmail).setHeader("Correo Electrónico");
        userGrid.addColumn(AppUser::getAcademicPosition).setHeader("Posición Académica");
        userGrid.addColumn(AppUser::getCenter).setHeader("Centro");
        userGrid.addColumn(AppUser::getTechnicalArea).setHeader("Área Técnica");
        userGrid.addColumn(AppUser::getIsAdmin).setHeader("Es Administrador");

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

        TextField usernameField = new TextField("Nombre y apellidos");
        TextField emailField = new TextField("Correo Electrónico");
        PasswordField passwordField = new PasswordField("Contraseña");

        ComboBox<String> positionField = new ComboBox<>("Posición Académica");
        positionField.setItems("Decano", "Rector", "Técnico", "Vicerector", "Profesor");

        TextField areaField = new TextField("Área Técnica");

        TextField centerField = new TextField("Centro");
        TextField isAdminField = new TextField("Es Administrador");

        binder.forField(usernameField).bind(AppUser::getUsername, AppUser::setUsername);
        binder.forField(emailField).bind(AppUser::getEmail, AppUser::setEmail);
        binder.forField(passwordField).bind(AppUser::getPassword, AppUser::setPassword);
        binder.forField(positionField).bind(AppUser::getAcademicPosition, AppUser::setAcademicPosition);
        binder.forField(areaField).bind(AppUser::getTechnicalArea, AppUser::setTechnicalArea);
        binder.forField(isAdminField).withConverter(Boolean::parseBoolean, String::valueOf).bind(AppUser::getIsAdmin, AppUser::setisAdmin);

        Button saveButton = new Button("Guardar", e -> saveUser());
        Button cancelButton = new Button("Cancelar", e -> cancelEdit());
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);

        FormLayout formLayout = new FormLayout();
        formLayout.add(
                usernameField, emailField,
                passwordField, positionField,
                areaField, centerField,
                isAdminField
        );

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 2)
        );

        formLayout.setColspan(buttonLayout, 2);
        formLayout.add(buttonLayout);

        formLayout.setWidth("70%");
        formLayout.addClassName("custom-form");
        formLayout.getStyle().set("margin-left", "50px");

        formLayoutContainer.add(formLayout);
        return formLayoutContainer;
    }


    private void updateGrid() {
        userGrid.setItems(userRepository.findAll());
    }

    private void addNewUser() {
        currentUser = new AppUser(); // Crear un nuevo usuario vacío
        binder.readBean(currentUser); // Limpiar el formulario
    }

    private void editUser(AppUser user) {
        currentUser = user;
        binder.readBean(user);
    }

    private void saveUser() {
        try {
            if (currentUser != null) {
                binder.writeBean(currentUser);
                userRepository.save(currentUser);
                currentUser = null;
                updateGrid();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteUser(AppUser user) {
        userRepository.delete(user);
        updateGrid();
    }

    private void cancelEdit() {
        currentUser = null;
        binder.readBean(null); // Limpiar el formulario
    }
}

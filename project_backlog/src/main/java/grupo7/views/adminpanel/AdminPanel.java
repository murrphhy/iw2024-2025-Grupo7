package grupo7.views.adminpanel;

import ch.qos.logback.classic.encoder.JsonEncoder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import grupo7.models.AppUser;
import grupo7.models.Calls;
import grupo7.models.Role;
import grupo7.repositories.UserRepository;
import grupo7.services.CallService;
import grupo7.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import com.vaadin.flow.component.formlayout.FormLayout;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@PageTitle("Panel Administrador")
@Route("/admin-panel")
@Menu(order = 3)
@RolesAllowed("ADMINISTRATOR")
public class AdminPanel extends Div {

    private final Tabs tabs = new Tabs();
    private final Tab userTab = new Tab(getTranslation("users"));
    private final Tab callTab = new Tab(getTranslation("calls"));

    private final Div userLayout = new Div();
    private final Div callLayout = new Div();

    // Gestión de usuarios
    private final Grid<AppUser> userGrid = new Grid<>(AppUser.class, false);
    private final Binder<AppUser> binder = new Binder<>(AppUser.class);
    private AppUser currentUser;
    private final UserService userService;
    private FormLayout userFormLayout;

    // Gestión de convocatorias
    private final Grid<Calls> callGrid = new Grid<>(Calls.class, false);
    private final Binder<Calls> callBinder = new Binder<>(Calls.class);
    private Calls currentCall;
    private final CallService callService;
    private FormLayout callFormLayout;

    private PasswordField passwordField;

    public AdminPanel(UserService userService, CallService callService) {
        this.userService = userService;
        this.callService = callService;

        configureTabs();

        HorizontalLayout toolbar = createUserToolbar();

        configureUserGrid();
        configureCallGrid();

        Div userForm = createUserForm();
        Div callForm = createCallForm();

        userLayout.add(createUserToolbar(), userGrid, userForm);
        callLayout.add(createCallToolbar(), callGrid, callForm);

        add(tabs, userLayout, callLayout);

        updateUserGrid();
        updateCallGrid();

        showLayout(userLayout);
    }

    private void configureTabs() {
        tabs.add(userTab, callTab);
        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab() == userTab) {
                showLayout(userLayout);
            } else {
                showLayout(callLayout);
            }
        });
    }

    private void showLayout(Div layout) {
        userLayout.setVisible(layout == userLayout);
        callLayout.setVisible(layout == callLayout);
        System.out.println("Mostrando layout: " + (layout == userLayout ? "Usuarios" : "Convocatorias"));
    }

    private void configureUserGrid() {
        userGrid.addColumn(AppUser::getUsername).setHeader(getTranslation("username"));
        userGrid.addColumn(AppUser::getEmail).setHeader(getTranslation("email"));
        userGrid.addColumn(AppUser::getAcademicPosition).setHeader(getTranslation("academicPosition"));
        userGrid.addColumn(AppUser::getCenter).setHeader(getTranslation("center"));
        userGrid.addColumn(AppUser::getTechnicalArea).setHeader(getTranslation("technicalArea"));
        userGrid.addColumn(AppUser::getRole).setHeader(getTranslation("role"));

        userGrid.addComponentColumn(user -> {
            Button editButton = new Button(getTranslation("action.edit"), e -> editUser(user));
            Button deleteButton = new Button(getTranslation("action.delete"), e -> {
                deleteUser(user);
                Notification.show("Has eliminado el usuario con éxito", 3000, Notification.Position.MIDDLE);
            });
            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            return actions;
        }).setHeader(getTranslation("actions"));


        userGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }

    private Div createUserForm() {

        Div formLayoutContainer = new Div();

        // Formulario principal
        userFormLayout = new FormLayout();
        userFormLayout.setWidth("70%");

        // Configuración de campos
        TextField usernameField = new TextField(getTranslation("form.username"));
        TextField emailField = new TextField(getTranslation("email"));
        passwordField = new PasswordField(getTranslation("password"));
        ComboBox<String> positionField = new ComboBox<>(getTranslation("academicPosition"));
        positionField.setItems("Decano", "Rector", "Técnico", "Vicerector", "Profesor");
        TextField areaField = new TextField(getTranslation("technicalArea"));
        TextField centerField = new TextField(getTranslation("center"));
        ComboBox<Role> roleField = new ComboBox<>(getTranslation("role"));
        roleField.setItems(Role.values());
        roleField.setItemLabelGenerator(Role::name);

        binder.forField(usernameField)
                .asRequired(getTranslation("usernameRequired"))
                .bind(AppUser::getUsername, AppUser::setUsername);

        binder.forField(emailField)
                .asRequired(getTranslation("emailRequired"))
                .bind(AppUser::getEmail, AppUser::setEmail);

        binder.forField(passwordField)
                .withValidator(pass -> pass.length() >= 6 || pass.isEmpty(),
                        getTranslation("passwordLength"))
                .bind(
                        user -> "",
                        (user, newPassword) -> {
                            if (!newPassword.isEmpty()) {
                                user.setPassword(newPassword);
                            }
                        }
                );

        binder.forField(positionField)
                .asRequired(getTranslation("positionRequired"))
                .bind(AppUser::getAcademicPosition, AppUser::setAcademicPosition);

        binder.forField(areaField)
                .bind(AppUser::getTechnicalArea, AppUser::setTechnicalArea);

        binder.forField(centerField)
                .asRequired(getTranslation("centerRequired"))
                .bind(AppUser::getCenter, AppUser::setCenter);

        binder.forField(roleField)
                .bind(AppUser::getRole, AppUser::setRole);

        Button saveButton = new Button(getTranslation("button.save"), e -> saveUser());
        Button cancelButton = new Button(getTranslation("cancel"), e -> cancelUserEdit());
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);

        userFormLayout.add(
                usernameField, emailField,
                passwordField, positionField,
                areaField, centerField,
                roleField, buttonLayout
        );
        userFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        userFormLayout.setColspan(buttonLayout, 2);

        userFormLayout.setVisible(false);

        formLayoutContainer.add(userFormLayout);
        userFormLayout.getStyle().set("margin-left", "50px");

        return formLayoutContainer;
    }

    private HorizontalLayout createUserToolbar() {
        Button addNewUserButton = new Button(getTranslation("newUser"), e -> addNewUser());
        return new HorizontalLayout(addNewUserButton);
    }

    private void updateUserGrid() {
        userGrid.setItems(userService.getAllUsers());
    }

    private void addNewUser() {
        currentUser = new AppUser();
        currentUser.setAcademicPosition("Profesor");
        currentUser.setCenter("Centro predeterminado");
        binder.readBean(currentUser);
        userFormLayout.setVisible(true);
    }

    private void editUser(AppUser user) {
        if (user != null) {
            currentUser = user;

            // Leer datos en el formulario
            binder.readBean(user);

            // Ocultar el campo de contraseña
            passwordField.setVisible(false);

            // Hacer visible el formulario
            userFormLayout.setVisible(true);

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
                updateUserGrid();
                userFormLayout.setVisible(false);
            }
        } catch (ValidationException e) {
            e.printStackTrace();
            Notification.show(getTranslation("validationError"));
        }
        passwordField.setVisible(true);
    }

    private void deleteUser(AppUser user) {
        userService.deleteUser(user.getId());
        updateUserGrid();
    }

    private void cancelUserEdit() {
        currentUser = null;
        passwordField.setVisible(true);
        binder.readBean(null);
        userFormLayout.setVisible(false);
    }

    // Gestión de convocatorias
    private void configureCallGrid() {
        callGrid.addColumn(Calls::getName).setHeader(getTranslation("name"));
        callGrid.addColumn(Calls::getDescription).setHeader(getTranslation("description"));
        callGrid.addColumn(Calls::getState).setHeader(getTranslation("state"));
        callGrid.addColumn(Calls::getTotalBudget).setHeader(getTranslation("totalbudget"));

        callGrid.addComponentColumn(call -> {
            Button editButton = new Button(getTranslation("action.edit"), e -> editCall(call));
            Button deleteButton = new Button(getTranslation("action.delete"), e -> deleteCall(call));
            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader(getTranslation("actions"));

        callGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }

    private Div createCallForm() {

        callFormLayout = new FormLayout();
        callFormLayout.setWidth("70%");

        TextField nameField = new TextField(getTranslation("name"));
        TextField descriptionField = new TextField(getTranslation("description"));
        ComboBox<String> stateComboBox = new ComboBox<>(getTranslation("state"));
        stateComboBox.setItems("Abierta", "Cerrada");
        TextField totalBudgetField = new TextField(getTranslation("totalbudget"));

        callBinder.forField(nameField)
                .asRequired(getTranslation("callnameRequired"))
                .bind(Calls::getName, Calls::setName);
        
        callBinder.forField(descriptionField)
                .asRequired(getTranslation("descriptionRequired"))
                .bind(Calls::getDescription, Calls::setDescription);

        callBinder.forField(stateComboBox)
                .asRequired(getTranslation("stateRequired"))
                .bind(Calls::getState, Calls::setState);
        
        callBinder.forField(totalBudgetField)
            .asRequired(getTranslation("budgetRequired"))
            .withNullRepresentation("")
            .withConverter(
                    new StringToDoubleConverter("Debe ser un número válido"))
            .bind(Calls::getTotalBudget, Calls::setTotalBudget);

        Button saveButton = new Button(getTranslation("button.save"), e -> saveCall());
        Button cancelButton = new Button(getTranslation("cancel"), e -> cancelCallEdit());
        callFormLayout.add(nameField, descriptionField, stateComboBox, totalBudgetField, new HorizontalLayout(saveButton, cancelButton));

        callFormLayout.setVisible(false);

        return new Div(callFormLayout);
    }

    private HorizontalLayout createCallToolbar() {
        Button addCallButton = new Button(getTranslation("newcall"), e -> addNewCall());
        return new HorizontalLayout(addCallButton);
    }

    private void updateCallGrid() {
        callGrid.setItems(callService.getAllCalls());
    }

    private void addNewCall() {
        try {
            currentCall = new Calls();
            callBinder.readBean(currentCall);
            callFormLayout.setVisible(true);
            System.out.println("Formulario de nueva convocatoria mostrado.");
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error al inicializar el formulario de convocatoria");
        }
    }

    private void editCall(Calls call) {
        currentCall = call;
        callBinder.readBean(call);
        callFormLayout.setVisible(true);
    }

    private void saveCall() {
        try {
            if (currentCall != null) {
                callBinder.writeBean(currentCall);
                System.out.println("Descripción al guardar: " + currentCall.getDescription());
                System.out.println("Datos de convocatoria: " + currentCall);
                callService.saveCall(currentCall);
                currentCall = null;
                updateCallGrid();
                callFormLayout.setVisible(false);
            }
        } catch (ValidationException e) {
            e.printStackTrace();
            Notification.show(getTranslation("validationError"));
        }
    }
    

    private void deleteCall(Calls call) {
        callService.deleteCall(call.getId());
        updateCallGrid();
    }

    private void cancelCallEdit() {
        currentCall = null;
        callBinder.readBean(null);
        callFormLayout.setVisible(false);
    }
}
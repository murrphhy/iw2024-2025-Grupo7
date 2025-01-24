package grupo7.views.MyProjects;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import grupo7.models.Calls;
import grupo7.models.Project;
import grupo7.security.AuthenticatedUser;
import grupo7.services.CallService;
import grupo7.services.ProjectService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * View to display projects requested by the authenticated user.
 */
@PageTitle("Mis Proyectos")
@Route("/my-projects")
@Menu(order = 4)
@PermitAll
public class MyProjectsView extends VerticalLayout {

    private CallService callService;
    private final AuthenticatedUser authenticatedUser;
    private final ProjectService projectService;
    private final Grid<Project> projectGrid;
    private ComboBox<Calls> callFilterComboBox;

    @Autowired
    public MyProjectsView(AuthenticatedUser authenticatedUser, ProjectService projectService, CallService callService) {
        this.authenticatedUser = authenticatedUser;
        this.projectService = projectService;
        this.callService = callService;

        createCallFilterComboBox();

        // Create and configure the grid
        projectGrid = new Grid<>(Project.class, false);
        configureGrid();
        
        // Add components to the layout
        add(callFilterComboBox,projectGrid);

        // Configure the layout
        setSizeFull();
        setSpacing(true);
        setPadding(true);

        // Load projects for the authenticated user
        loadProjects();
    }

    // Método para crear el ComboBox de convocatorias
    private void createCallFilterComboBox() {
        // Crear el ComboBox de convocatorias
        callFilterComboBox = new ComboBox<>();
        callFilterComboBox.setLabel(getTranslation("filterByCall"));
        callFilterComboBox.setItemLabelGenerator(Calls::getName);  // Mostrar el nombre de la convocatoria

        // Llamamos al servicio para cargar las convocatorias
        callFilterComboBox.setItems(callService.getAllCalls());
        callFilterComboBox.addValueChangeListener(e -> filterProjectsByCall(e.getValue()));  // Filtrar proyectos por convocatoria seleccionada
    }

    private void filterProjectsByCall(Calls selectedCall) {
        if (selectedCall != null) {
            // Filtrar proyectos por convocatoria seleccionada
            projectGrid.setItems(projectService.getProjectsByCall(selectedCall.getId()));
        } else {
            // Si no se selecciona ninguna convocatoria, mostrar todos los proyectos
            projectGrid.setItems(projectService.getAllProjects());
        }
    }

    /**
     * Configures the grid to display project details.
     */
    private void configureGrid() {
        projectGrid.addColumn(Project::getTitle).setHeader(getTranslation("title")).setSortable(true);
        projectGrid.addColumn(Project::getState).setHeader(getTranslation("state")).setSortable(true);
        projectGrid.addColumn(Project::getStartDate).setHeader(getTranslation("startDate"));
        projectGrid.addColumn(project -> project.getApplicantId() != null ? project.getApplicantId().getUsername() : getTranslation("notAvailable"))
                .setHeader(getTranslation("applicant"));

    }

    /**
     * Loads the projects associated with the authenticated user and displays them in the grid.
     */
    private void loadProjects() {
        authenticatedUser.get().ifPresentOrElse(user -> {
            List<Project> userProjects = projectService.getProjectsByUserId(user.getId());
            projectGrid.setItems(userProjects);
        }, () -> {
            projectGrid.setItems(); // Clear the grid

            add(new Span(getTranslation("auth.noProjectsAssociated")));
        });
    }
}


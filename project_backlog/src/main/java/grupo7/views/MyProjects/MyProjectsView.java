package grupo7.views.MyProjects;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import grupo7.models.Project;
import grupo7.security.AuthenticatedUser;
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

    private final AuthenticatedUser authenticatedUser;
    private final ProjectService projectService;
    private final Grid<Project> projectGrid;

    @Autowired
    public MyProjectsView(AuthenticatedUser authenticatedUser, ProjectService projectService) {
        this.authenticatedUser = authenticatedUser;
        this.projectService = projectService;

        // Configure the layout
        setSizeFull();
        setSpacing(true);
        setPadding(true);

        // Create and configure the grid
        projectGrid = new Grid<>(Project.class, false);
        configureGrid();

        // Add components to the layout
        add(projectGrid);

        // Load projects for the authenticated user
        loadProjects();
    }

    /**
     * Configures the grid to display project details.
     */
    private void configureGrid() {
        projectGrid.addColumn(Project::getTitle).setHeader("Título").setSortable(true);
        projectGrid.addColumn(Project::getState).setHeader("Estado").setSortable(true);
        projectGrid.addColumn(Project::getStartDate).setHeader("Fecha de Inicio");
        projectGrid.addColumn(project -> project.getApplicantId() != null ? project.getApplicantId().getUsername() : "N/A")
                .setHeader("Solicitante");
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
            add(new Span("No estás autenticado o no tienes proyectos asociados."));
        });
    }
}

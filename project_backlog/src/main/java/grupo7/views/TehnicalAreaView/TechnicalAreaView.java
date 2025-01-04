package grupo7.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import grupo7.models.Project;
import grupo7.services.ProjectService;
import grupo7.services.TechnicianProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import org.springframework.security.access.prepost.PreAuthorize;

@Route("/technical-area")
@PreAuthorize("hasRole('TECHNICIAN')")
public class TechnicalAreaView extends VerticalLayout {

    private final ProjectService projectService;
    private final TechnicianProjectService technicianProjectService;

    private final Grid<Project> projectGrid;

    @Autowired
    public TechnicalAreaView(ProjectService projectService, TechnicianProjectService technicianProjectService) {
        this.projectService = projectService;
        this.technicianProjectService = technicianProjectService;

        // Inicializar la tabla
        projectGrid = new Grid<>(Project.class, false);
        configureGrid();

        // Añadir componentes al diseño principal
        add(projectGrid);

        // Cargar datos en la tabla
        loadProjects();
    }

    private void configureGrid() {
        // Añadir columnas
        projectGrid.addColumn(Project::getTitle).setHeader("Nombre del Proyecto").setSortable(true);
        projectGrid.addColumn(project -> getCioRating(project.getId())).setHeader("Calificación del CIO");
        projectGrid.addColumn(Project::getState).setHeader("Estado").setSortable(true);

        // Columna de acciones para puntuar
        projectGrid.addComponentColumn(project -> {
            Button rateButton = new Button("Puntuar");
            rateButton.addClickListener(click -> openRatingDialog(project));
            return rateButton;
        }).setHeader("Acciones");
    }

    private void loadProjects() {
        projectGrid.setItems(projectService.getAllProjects());
    }

    private String getCioRating(Long projectId) {
        Optional<Integer> rating = projectService.getCioRating(projectId);
        return rating.map(String::valueOf).orElse("No disponible");
    }

    private void openRatingDialog(Project project) {
        // Aquí puedes abrir un cuadro de diálogo para ingresar la puntuación técnica.
        Notification.show("Abrir cuadro de diálogo para puntuar el proyecto: " + project.getTitle());
    }
}

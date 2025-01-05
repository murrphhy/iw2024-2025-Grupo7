package grupo7.views.TehnicalAreaView;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import grupo7.models.Project;
import grupo7.services.ProjectService;
import grupo7.services.TechnicianProjectService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@PageTitle("Technical-View")
@Route("/technical-area")
@Menu(order = 2)
@RolesAllowed("TECHNICAL")
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
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");
        dialog.setHeight("300px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add("Puntuar Proyecto: " + project.getTitle());

        // Campo para ingresar la puntuación
        NumberField ratingField = new NumberField("Puntuación Técnica");
        ratingField.setMin(0);
        ratingField.setMax(10);
        dialogLayout.add(ratingField);

        // Botón para guardar la puntuación
        Button saveButton = new Button("Guardar", event -> {
            if (ratingField.getValue() != null) {
                Double rating = ratingField.getValue();
                saveTechnicalRating(project.getId(), rating.intValue());
                Notification.show("Puntuación guardada: " + rating);
                dialog.close();
                loadProjects(); // Recargar datos en la tabla
            } else {
                Notification.show("Por favor, ingresa una puntuación válida.");
            }
        });

        // Botón para cancelar
        Button cancelButton = new Button("Cancelar", event -> dialog.close());

        dialogLayout.add(saveButton, cancelButton);
        dialog.add(dialogLayout);

        dialog.open();
    }

    private void saveTechnicalRating(Long projectId, int rating) {
        Long userId = getAuthenticatedUserId(); // Obtener el ID del usuario autenticado
        technicianProjectService.saveTechnicalRating(userId, projectId, rating);
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Supongamos que el nombre del usuario autenticado es su ID
            return Long.valueOf(authentication.getName());
        }
        throw new IllegalStateException("Usuario no autenticado.");
    }
}

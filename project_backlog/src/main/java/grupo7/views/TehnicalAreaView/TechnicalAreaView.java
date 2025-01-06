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
        projectGrid.addColumn(Project::getStrategicAlignment).setHeader("Calificación del CIO");
        projectGrid.addColumn(Project::getState).setHeader("Estado").setSortable(true);

        // Columna de acciones para puntuar
        projectGrid.addComponentColumn(project -> {
            Button rateButton = new Button("Evaluar");
            rateButton.addClickListener(click -> openRatingDialog(project));
            return rateButton;
        }).setHeader("Acciones");
    }

    private void loadProjects() {
        projectGrid.setItems(projectService.getAllProjects());
    }


    private void openRatingDialog(Project project) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setHeight("400px");

        // Layout principal para el modal
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setSizeFull();

        // Contenedor para la información del proyecto (scrollable)
        VerticalLayout projectInfoLayout = new VerticalLayout();
        projectInfoLayout.setPadding(true);
        projectInfoLayout.setSpacing(true);
        projectInfoLayout.setSizeFull();
        projectInfoLayout.getStyle().set("overflow-y", "auto"); // Habilitar scroll vertical

        // Añadir información del proyecto
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span("Título: " + project.getTitle()));
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span("Promotor/a: " + project.getPromoterId()));
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span("Alcance: " + project.getScope()));
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span("Fecha de comienzo: " + project.getStartDate()));
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span("Memoria: " + project.getMemory())); //Archivo PDF, que haya un boton para descargarla
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span("Especificaciones técnicas: " + project.getTechnicalSpecifications())); //Archivo PDF, que haya un boton para descargarla
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span("Regulaciones del proyecto: " + project.getProjectRegulations())); //Archivo PDF, que haya un boton para descargarla



        // Contenedor fijo para controles (campo y botones)
        VerticalLayout controlsLayout = new VerticalLayout();
        controlsLayout.setPadding(true);
        controlsLayout.setSpacing(true);
        controlsLayout.getStyle().set("border-top", "1px solid #ccc"); // Separador visual

        // Campo para ingresar la puntuación
        NumberField ratingField = new NumberField("Puntuación Técnica");
        ratingField.setMin(0);
        ratingField.setMax(10);
        controlsLayout.add(ratingField);

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

        // Agregar botones al contenedor fijo
        controlsLayout.add(saveButton, cancelButton);

        // Combinar las secciones
        dialogLayout.addAndExpand(projectInfoLayout); // Expandible (scrollable)
        dialogLayout.add(controlsLayout); // Fijo

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
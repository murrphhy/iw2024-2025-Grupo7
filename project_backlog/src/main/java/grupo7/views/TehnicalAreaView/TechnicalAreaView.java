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


/**
 * Vista de área técnica para técnicos.
 * Permite listar proyectos y evaluarlos técnicamente con una puntuación.
 */
@PageTitle("Technical-View")
@Route("/technical-area")
@Menu(order = 2)
@RolesAllowed("TECHNICAL")
public class TechnicalAreaView extends VerticalLayout {

    private final ProjectService projectService;
    private final TechnicianProjectService technicianProjectService;
    private final Grid<Project> projectGrid;

    /**
     * Constructor de la vista de área técnica.
     *
     * @param projectService           Servicio para gestionar proyectos.
     * @param technicianProjectService Servicio para gestionar evaluaciones técnicas de proyectos.
     */
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

    /**
     * Configura la tabla que muestra los proyectos, incluyendo columnas y acciones.
     */
    private void configureGrid() {
        projectGrid.addColumn(Project::getTitle).setHeader("Nombre del Proyecto").setSortable(true);
        projectGrid.addColumn(Project::getStrategicAlignment).setHeader("Calificación del CIO");
        projectGrid.addColumn(Project::getState).setHeader("Estado").setSortable(true);

        // Columna de acciones para puntuar proyectos
        projectGrid.addComponentColumn(project -> {
            Button rateButton = new Button("Evaluar");
            rateButton.addClickListener(click -> openRatingDialog(project));
            return rateButton;
        }).setHeader("Acciones");
    }

    /**
     * Carga la lista de proyectos en la tabla desde el servicio de proyectos.
     */
    private void loadProjects() {
        projectGrid.setItems(projectService.getAllProjects());
    }

    /**
     * Abre un cuadro de diálogo para evaluar un proyecto técnicamente.
     *
     * @param project Proyecto a evaluar.
     */
    private void openRatingDialog(Project project) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setHeight("400px");

        // Layout principal del diálogo
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setSizeFull();

        // Contenedor de información del proyecto
        VerticalLayout projectInfoLayout = new VerticalLayout();
        projectInfoLayout.setPadding(true);
        projectInfoLayout.setSpacing(true);
        projectInfoLayout.setSizeFull();
        projectInfoLayout.getStyle().set("overflow-y", "auto");

        // Información del proyecto
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span("Título: " + project.getTitle()));
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span("Promotor/a: " + project.getPromoterId()));
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span("Alcance: " + project.getScope()));
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span("Fecha de comienzo: " + project.getStartDate()));
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span("Memoria: " + project.getMemory())); // Archivo PDF
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span("Especificaciones técnicas: " + project.getTechnicalSpecifications())); // Archivo PDF
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span("Regulaciones del proyecto: " + project.getProjectRegulations())); // Archivo PDF

        // Controles del cuadro de diálogo
        VerticalLayout controlsLayout = new VerticalLayout();
        controlsLayout.setPadding(true);
        controlsLayout.setSpacing(true);
        controlsLayout.getStyle().set("border-top", "1px solid #ccc");

        // Campo para ingresar la puntuación
        NumberField ratingField = new NumberField("Puntuación Técnica");
        ratingField.setMin(0);
        ratingField.setMax(10);
        controlsLayout.add(ratingField);

        // Botones
        Button saveButton = new Button("Guardar", event -> {
            if (ratingField.getValue() != null) {
                Double rating = ratingField.getValue();
                saveTechnicalRating(project.getId(), rating.intValue());
                Notification.show("Puntuación guardada: " + rating);
                dialog.close();
                loadProjects(); // Recargar proyectos
            } else {
                Notification.show("Por favor, ingresa una puntuación válida.");
            }
        });

        Button cancelButton = new Button("Cancelar", event -> dialog.close());

        // Añadir botones a los controles
        controlsLayout.add(saveButton, cancelButton);

        // Añadir secciones al cuadro de diálogo
        dialogLayout.addAndExpand(projectInfoLayout);
        dialogLayout.add(controlsLayout);

        dialog.add(dialogLayout);
        dialog.open();
    }

    /**
     * Guarda la puntuación técnica para un proyecto específico.
     *
     * @param projectId ID del proyecto a evaluar.
     * @param rating    Puntuación técnica asignada.
     */
    private void saveTechnicalRating(Long projectId, int rating) {
        Long userId = getAuthenticatedUserId();
        technicianProjectService.saveTechnicalRating(userId, projectId, rating);
    }

    /**
     * Obtiene el ID del usuario autenticado.
     *
     * @return ID del usuario autenticado.
     * @throws IllegalStateException Si no hay un usuario autenticado.
     */
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return Long.valueOf(authentication.getName());
        }
        throw new IllegalStateException("Usuario no autenticado.");
    }
}

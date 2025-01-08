package grupo7.views.TehnicalAreaView;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import grupo7.models.Project;
import grupo7.services.ProjectService;
import grupo7.services.TechnicianProjectService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayInputStream;


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

    private VerticalLayout createDownloadField(String label, byte[] fileContent, String fileName) {
        VerticalLayout fieldLayout = new VerticalLayout();
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);

        Paragraph labelComponent = new Paragraph(label);
        labelComponent.getStyle()
                .set("color", "darkgray")
                .set("font-weight", "bold")
                .set("margin-bottom", "0")
                .set("text-align", "left");

        StreamResource resource = new StreamResource(
                fileName,
                () -> new ByteArrayInputStream(fileContent)
        );
        resource.setContentType("application/pdf"); // Adjust the type according to the file

        Anchor downloadLink = new Anchor(resource, "Descargar " + label);
        downloadLink.getElement().setAttribute("download", true);
        downloadLink.getStyle().set("margin-top", "5px");
        downloadLink.getStyle().set("display", "inline-block");

        fieldLayout.add(labelComponent, downloadLink);

        return fieldLayout;
    }


    private VerticalLayout createNoFileMessage(String label) {
        VerticalLayout fieldLayout = new VerticalLayout();
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);

        Paragraph labelComponent = new Paragraph(label);
        labelComponent.getStyle()
                .set("color", "darkgray")
                .set("font-weight", "bold")
                .set("margin-bottom", "0")
                .set("text-align", "left");

        Paragraph noFileMessage = new Paragraph("No se ha subido ningún archivo.");
        noFileMessage.getStyle()
                .set("font-size", "16px")
                .set("font-weight", "normal")
                .set("margin-top", "5px");

        fieldLayout.add(labelComponent, noFileMessage);
        return fieldLayout;
    }


    /**
     * Configura la tabla que muestra los proyectos, incluyendo columnas y acciones.
     */
    private void configureGrid() {
        projectGrid.addColumn(Project::getTitle).setHeader("Nombre del Proyecto").setSortable(true);
        projectGrid.addColumn(Project::getStrategicAlignment).setHeader("Calificación del CIO");
        projectGrid.addColumn(Project::getState).setHeader("Estado").setSortable(true);


        projectGrid.addComponentColumn(project -> {
            Button rateButton = new Button("Evaluar");

            // Habilitar el botón solo si el estado del proyecto es "alineado"
            rateButton.setEnabled("alineado".equalsIgnoreCase(project.getState()));

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
        dialog.setWidth("65%");
        dialog.setHeight("75%");

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
        // Download Memory file if exists
        projectInfoLayout.add(createDownloadField("Memoria", project.getMemory(), project.getShortTitle() + "_memory.pdf"));

        // Add buttons for Project Regulations and Technical Specifications
        if (project.getProjectRegulations() != null && project.getProjectRegulations().length > 0) {
            projectInfoLayout.add(createDownloadField("Regulaciones del Proyecto", project.getProjectRegulations(), project.getShortTitle() + "_project_regulations.pdf"));
        } else {
            projectInfoLayout.add(createNoFileMessage("Regulaciones del Proyecto"));
        }

        if (project.getTechnicalSpecifications() != null && project.getTechnicalSpecifications().length > 0) {
            projectInfoLayout.add(createDownloadField("Especificaciones Técnicas", project.getTechnicalSpecifications(), project.getShortTitle() + "_technical_specifications.pdf"));
        } else {
            projectInfoLayout.add(createNoFileMessage("Especificaciones Técnicas"));
        }

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
                technicianProjectService.saveTechnicalRating(project.getApplicantId().getId(),project.getId(),rating);
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

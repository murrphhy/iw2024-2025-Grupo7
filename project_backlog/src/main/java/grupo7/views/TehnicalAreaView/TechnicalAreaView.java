package grupo7.views.TehnicalAreaView;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextArea;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Vista de área técnica para técnicos.
 * Permite listar proyectos y evaluarlos técnicamente con una puntuación.
 */
@PageTitle("Evaluar Proyectos")
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

        refreshGrid();
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

        Anchor downloadLink = new Anchor(resource, getTranslation("button.download.Memory"));
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

        Paragraph noFileMessage = new Paragraph(getTranslation("no.file.uploaded"));
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
        projectGrid.addColumn(Project::getTitle).setHeader(getTranslation("evaluate.title")).setSortable(true);
        projectGrid.addColumn(Project::getStrategicAlignment).setHeader(getTranslation("note"));
        projectGrid.addColumn(Project::getState).setHeader(getTranslation("state")).setSortable(true);

        projectGrid.addComponentColumn(project -> {
            Button rateButton = new Button(getTranslation("evaluateButton"));

            // Habilitar el botón solo si el estado del proyecto es "alineado"
            rateButton.setEnabled("alineado".equalsIgnoreCase(project.getState()));

            rateButton.addClickListener(click -> openRatingDialog(project));

            return rateButton;
        }).setHeader(getTranslation("evaluate.title"));
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
        dialog.setHeight("100%");

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
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span(getTranslation("title") + ": " + project.getTitle()));
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span(getTranslation("promoter") + ": " + project.getPromoterId()));
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span(getTranslation("scope") + ": " + project.getScope()));
        projectInfoLayout.add(new com.vaadin.flow.component.html.Span(getTranslation("startDate") + ": " + project.getStartDate()));
        // Download Memory file if exists
        projectInfoLayout.add(createDownloadField(getTranslation("button.download.Memory"), project.getMemory(), project.getShortTitle() + "_memory.pdf"));

        // Añadir botones para descargar archivos relacionados
        if (project.getProjectRegulations() != null && project.getProjectRegulations().length > 0) {
            projectInfoLayout.add(createDownloadField(getTranslation("button.download.Regulations"), project.getProjectRegulations(), project.getShortTitle() + "_project_regulations.pdf"));
        } else {
            projectInfoLayout.add(createNoFileMessage(getTranslation("project.regulations")));
        }

        if (project.getTechnicalSpecifications() != null && project.getTechnicalSpecifications().length > 0) {
            projectInfoLayout.add(createDownloadField(getTranslation("button.download.Specifications"), project.getTechnicalSpecifications(), project.getShortTitle() + "_technical_specifications.pdf"));
        } else {
            projectInfoLayout.add(createNoFileMessage(getTranslation("technical.specifications")));
        }

        // Controles del cuadro de diálogo
        VerticalLayout controlsLayout = new VerticalLayout();
        controlsLayout.setPadding(true);
        controlsLayout.setSpacing(true);
        controlsLayout.getStyle().set("border-top", "1px solid #ccc");

        // Campo para ingresar la puntuación
        NumberField ratingField = new NumberField(getTranslation(getTranslation("rating")));
        ratingField.setMin(0);
        ratingField.setMax(10);
        controlsLayout.add(ratingField);

        // Campo para recursos humanos
        NumberField humanResourcesField = new NumberField(getTranslation("resources.human"));
        humanResourcesField.setMin(0);

        TextArea humanResourcesCommentField = new TextArea(getTranslation("Comentario sobre recursos humanos"));
        humanResourcesCommentField.setPlaceholder(getTranslation("Deja tu comentario aquí..."));
        humanResourcesCommentField.setWidthFull();

        // Agregar los campos de recursos humanos al layout
        controlsLayout.add(humanResourcesField, humanResourcesCommentField);

        // Campo para recursos financieros
        NumberField financialResourcesField = new NumberField(getTranslation("resources.financial"));
        financialResourcesField.setMin(0);

        TextArea financialResourcesCommentField = new TextArea(getTranslation("Comentario sobre recursos financieros"));
        financialResourcesCommentField.setPlaceholder(getTranslation("Deja tu comentario aquí..."));
        financialResourcesCommentField.setWidthFull();

        // Agregar los campos de recursos financieros al layout
        controlsLayout.add(financialResourcesField, financialResourcesCommentField);

        // Crear un layout horizontal para los botones
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true); // Agregar espacio entre los botones

        // Botón para guardar
        Button saveButton = new Button(getTranslation("button.save"), event -> {
            // Comprobar que los valores numéricos no sean nulos
            if (ratingField.getValue() != null && humanResourcesField.getValue() != null && financialResourcesField.getValue() != null) {
                Double rating = ratingField.getValue();
                int humanResources = humanResourcesField.getValue().intValue();  // Convertir a int
                BigDecimal financialResources = BigDecimal.valueOf(financialResourcesField.getValue());  // Convertir a BigDecimal

                // Obtener los comentarios de los recursos (deben ser campos de texto, como TextArea)
                String humanResourcesComment = humanResourcesCommentField.getValue();  // Comentario de recursos humanos
                String financialResourcesComment = financialResourcesCommentField.getValue();  // Comentario de recursos financieros

                // Llamada al servicio para guardar la evaluación
                technicianProjectService.saveTechnicalRating(
                        project.getApplicantId().getId(),
                        project.getId(),
                        rating,
                        humanResources,
                        financialResources,
                        humanResourcesComment,
                        financialResourcesComment
                );

                // Mostrar notificación con los comentarios
                Notification.show(
                        getTranslation("notification.savedEvaluation1") + rating + ", " +
                                getTranslation("notification.savedEvaluation2") + humanResources + ", "+ " (" + humanResourcesComment + ") " +
                                getTranslation("notification.savedEvaluation3") + financialResources + ", "  + " ("+ financialResourcesComment + ") " +
                                getTranslation("notification.savedEvaluation4") + "\n"
                );


                dialog.close();
                loadProjects(); // Recargar proyectos
                refreshGrid();
            } else {
                Notification.show(getTranslation("please.upload.field"));
            }
        });


        Button cancelButton = new Button(getTranslation("cancel"), event -> dialog.close());

        // Añadir botones al layout horizontal
        buttonsLayout.add(saveButton, cancelButton);

        // Añadir el layout de botones al controlsLayout
        controlsLayout.add(buttonsLayout);

        // Añadir secciones al cuadro de diálogo
        dialogLayout.addAndExpand(projectInfoLayout);
        dialogLayout.add(controlsLayout);

        dialog.add(dialogLayout);
        dialog.open();
    }


    /**
     * Refreshes the project grid by fetching and displaying only the projects with the state "alineado".
     */
    private void refreshGrid() {
        List<Project> alignedProjects = projectService.getAllProjects().stream()
                .filter(project -> "alineado".equalsIgnoreCase(project.getState()))
                .collect(Collectors.toList());
        projectGrid.setItems(alignedProjects);
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
        throw new IllegalStateException(getTranslation("no.logged.in"));
    }


}
package grupo7.views.CIO;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import grupo7.models.Project;
import grupo7.models.AppUser;
import grupo7.services.ProjectService;
import grupo7.services.EmailService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.List;

@PageTitle("Cio View")
@Route("cio-dashboard")
@RolesAllowed("CIO")
@Menu(order = 3)
public class CioView extends VerticalLayout {

    private final ProjectService projectService;
    private final EmailService emailService;
    private final Grid<Project> projectGrid = new Grid<>(Project.class);
    private final Binder<Project> binder = new Binder<>(Project.class);

    private final NumberField strategicAlignmentField = new NumberField("Alineamiento Estratégico");
    private final Button saveButton = new Button("Guardar");

    @Autowired
    public CioView(ProjectService projectService, EmailService emailService) {
        this.projectService = projectService;
        this.emailService = emailService;

        binder.forField(strategicAlignmentField)
                .bind(Project::getStrategicAlignment, Project::setStrategicAlignment);

        // Configurar layout
        setSizeFull();
        add(createProjectGrid(), createPrioritizationForm());
        refreshGrid();
    }

    private Grid<Project> createProjectGrid() {

        projectGrid.removeAllColumns();

        projectGrid.addColumn(project -> project.getApplicantId() != null ? project.getApplicantId().getUsername() : "N/A")
                .setHeader("Solicitante")
                .setSortable(true);

        projectGrid.addColumn(Project::getShortTitle)
                .setHeader("Título corto")
                .setSortable(true);

        projectGrid.addColumn(Project::getState)
                .setHeader("Estado")
                .setSortable(true);

        projectGrid.addColumn(project -> project.getStartDate() != null ? project.getStartDate().toString() : "N/A")
                .setHeader("Fecha")
                .setSortable(true);

        projectGrid.asSingleSelect().addValueChangeListener(event -> editProject(event.getValue()));
        projectGrid.addItemDoubleClickListener(event -> openProjectDetailsDialog(event.getItem()));

        return projectGrid;
    }

    private VerticalLayout createPrioritizationForm() {
        strategicAlignmentField.setMin(0);
        strategicAlignmentField.setMax(10);

        saveButton.addClickListener(event -> savePrioritization());

        HorizontalLayout formLayout = new HorizontalLayout(
                strategicAlignmentField,
                saveButton
        );
        formLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        return new VerticalLayout(formLayout);
    }

    private void editProject(Project project) {
        if (project == null) {
            clearForm();
            return;
        }
        binder.setBean(project);
    }

    private void openProjectDetailsDialog(Project project) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Detalles Proyecto");

        dialog.setWidth("60%");

        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.getStyle().set("text-align", "center");

        // Título principal
        H2 title = new H2(project.getTitle());
        title.getStyle().set("margin-bottom", "10px");
        detailsLayout.add(title);

        // Detalles estilizados
        detailsLayout.add(createDetailField("Título", project.getTitle()));
        detailsLayout.add(createDetailField("Promotor", project.getPromoterId()));
        detailsLayout.add(createDetailField("Solicitante",
                project.getApplicantId() != null ? project.getApplicantId().getUsername() : "N/A"));
        detailsLayout.add(createDetailField("Regulaciones", project.getProjectRegulations()));
        detailsLayout.add(createDetailField("Objetivo", project.getScope()));
        detailsLayout.add(createDetailField("Estado", project.getState()));
        detailsLayout.add(createDetailField("Fecha inicio", project.getStartDate() != null ? project.getStartDate().toString() : "N/A"));

        // Descargar archivo de memoria si existe
        if (project.getMemory() != null) {
            StreamResource resource = new StreamResource(
                    "memory.pdf",
                    () -> new ByteArrayInputStream(project.getMemory())
            );
            resource.setContentType("application/pdf");

            Button downloadButton = new Button("Descargar Memoria", e -> {
                Anchor downloadLink = new Anchor(resource, "");
                downloadLink.getElement().setAttribute("Descargar", true);
                downloadLink.getStyle().set("display", "none");
                detailsLayout.getElement().appendChild(downloadLink.getElement());
                downloadLink.getElement().callJsFunction("click");
            });
            detailsLayout.add(downloadButton);
        } else {
            detailsLayout.add(createDetailField("Memoria", "No memory file uploaded."));
        }

        Button closeButton = new Button("Cerrar", event -> dialog.close());
        HorizontalLayout footer = new HorizontalLayout(closeButton);
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.END);

        dialog.add(detailsLayout, footer);
        dialog.open();
    }

    // Metodo para crear un campo estilizado
    private VerticalLayout createDetailField(String label, String value) {
        VerticalLayout fieldLayout = new VerticalLayout();
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);

        Paragraph labelComponent = new Paragraph(label);
        labelComponent.getStyle()
                .set("color", "darkgray")
                .set("font-weight", "bold")
                .set("margin-bottom", "0");

        Paragraph valueComponent = new Paragraph(value);
        valueComponent.getStyle()
                .set("font-size", "16px")
                .set("font-weight", "normal")
                .set("margin-top", "0");

        fieldLayout.add(labelComponent, valueComponent);
        return fieldLayout;
    }

    private void savePrioritization() {
        Project project = binder.getBean();
        if (project != null) {


            if (project.getStrategicAlignment() != null) {

                project.setState("Puntuado");

                AppUser applicant = project.getApplicantId();

                if (applicant != null) {

                    String email = applicant.getEmail();
                    String subject = "Su proyecto ha sido puntuado";
                    String message = String.format(
                            "Estimado/a %s,\n\nSu proyecto titulado '%s' ha sido puntuado con el siguiente valor:\n" +
                                    "- Alineamiento Estratégico: %.1f\n\nGracias por su participación.",
                            applicant.getUsername(),
                            project.getTitle(),
                            strategicAlignmentField.getValue()
                    );

                    emailService.sendEmail(email, subject, message);
                }

            }else{
                project.setState("En espera");
            }
            projectService.saveProject(project);

            refreshGrid();
            clearForm();
        }
    }

    private void clearForm() {
        binder.setBean(null);
        strategicAlignmentField.clear();
    }

    private void refreshGrid() {
        List<Project> projects = projectService.getAllProjects();
        projectGrid.setItems(projects);
    }
}
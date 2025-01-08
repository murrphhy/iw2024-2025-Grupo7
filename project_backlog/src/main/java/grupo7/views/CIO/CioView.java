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
import com.vaadin.flow.i18n.I18NProvider;
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
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.ByteArrayInputStream;
import java.util.List;

@PageTitle("cio.view.title")
@Route("cio-dashboard")
@RolesAllowed("CIO")
@Menu(order = 3)
public class CioView extends VerticalLayout {

    private final ProjectService projectService;
    private final EmailService emailService;
    private final Grid<Project> projectGrid = new Grid<>(Project.class);
    private final Binder<Project> binder = new Binder<>(Project.class);

    private final NumberField strategicAlignmentField = new NumberField();
    private final Button saveButton = new Button();

    @Autowired
    public CioView(ProjectService projectService, EmailService emailService, I18NProvider i18nProvider) {
        this.projectService = projectService;
        this.emailService = emailService;

        binder.forField(strategicAlignmentField)
                .bind(Project::getStrategicAlignment, Project::setStrategicAlignment);

        // Localization
        strategicAlignmentField.setLabel(getTranslation("cio.view.field.alignment"));
        saveButton.setText(getTranslation("cio.view.button.save"));

        // Layout setup
        setSizeFull();
        add(createProjectGrid(), createPrioritizationForm());
        refreshGrid();
    }

    private Grid<Project> createProjectGrid() {
        projectGrid.removeAllColumns();

        projectGrid.addColumn(project -> project.getApplicantId() != null ? project.getApplicantId().getUsername() : getTranslation("cio.view.grid.notAvailable"))
                .setHeader(getTranslation("cio.view.grid.applicant"))
                .setSortable(true);

        projectGrid.addColumn(Project::getShortTitle)
                .setHeader(getTranslation("cio.view.grid.shortTitle"))
                .setSortable(true);

        projectGrid.addColumn(Project::getState)
                .setHeader(getTranslation("cio.view.grid.state"))
                .setSortable(true);

        projectGrid.addColumn(project -> project.getStartDate() != null ? project.getStartDate().toString() : getTranslation("cio.view.grid.notAvailable"))
                .setHeader(getTranslation("cio.view.grid.date"))
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
        dialog.setHeaderTitle(getTranslation("cio.view.dialog.details.title"));

        dialog.setWidth("60%");

        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.getStyle().set("text-align", "center");

        // Title
        H2 title = new H2(project.getTitle());
        title.getStyle().set("margin-bottom", "10px");
        detailsLayout.add(title);

        // Details
        detailsLayout.add(createDetailField(getTranslation("cio.view.dialog.details.field.title"), project.getTitle()));
        detailsLayout.add(createDetailField(getTranslation("cio.view.dialog.details.field.promoter"), project.getPromoterId()));
        detailsLayout.add(createDetailField(getTranslation("cio.view.dialog.details.field.applicant"),
                project.getApplicantId() != null ? project.getApplicantId().getUsername() : getTranslation("cio.view.grid.notAvailable")));
        detailsLayout.add(createDetailField(getTranslation("cio.view.dialog.details.field.objective"), project.getScope()));
        detailsLayout.add(createDetailField(getTranslation("cio.view.dialog.details.field.state"), project.getState()));
        detailsLayout.add(createDetailField(getTranslation("cio.view.dialog.details.field.startDate"), project.getStartDate() != null ? project.getStartDate().toString() : getTranslation("cio.view.grid.notAvailable")));

        // File download
        if (project.getMemory() != null) {
            StreamResource resource = new StreamResource(
                    "memory.pdf",
                    () -> new ByteArrayInputStream(project.getMemory())
            );
            resource.setContentType("application/pdf");

            Button downloadButton = new Button(getTranslation("cio.view.dialog.details.button.download"), e -> {
                Anchor downloadLink = new Anchor(resource, "");
                downloadLink.getElement().setAttribute("download", true);
                downloadLink.getStyle().set("display", "none");
                detailsLayout.getElement().appendChild(downloadLink.getElement());
                downloadLink.getElement().callJsFunction("click");
            });
            detailsLayout.add(downloadButton);
        } else {
            detailsLayout.add(createDetailField(getTranslation("cio.view.dialog.details.field.memory"), getTranslation("cio.view.dialog.details.noMemory")));
        }

        Button closeButton = new Button(getTranslation("cio.view.dialog.details.button.close"), event -> dialog.close());
        HorizontalLayout footer = new HorizontalLayout(closeButton);
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.END);

        dialog.add(detailsLayout, footer);
        dialog.open();
    }

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
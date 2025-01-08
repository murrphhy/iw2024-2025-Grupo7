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

@PageTitle("CIO")
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
    public CioView(ProjectService projectService, EmailService emailService) {
        this.projectService = projectService;
        this.emailService = emailService;

        binder.forField(strategicAlignmentField)
                .bind(Project::getStrategicAlignment, Project::setStrategicAlignment);

        // Localization
        strategicAlignmentField.setLabel(getTranslation("alignment"));
        saveButton.setText(getTranslation("button.save"));

        // Layout setup
        setSizeFull();
        add(createProjectGrid(), createPrioritizationForm());
        refreshGrid();
    }

    private Grid<Project> createProjectGrid() {
        projectGrid.removeAllColumns();

        projectGrid.addColumn(project -> project.getApplicantId() != null ? project.getApplicantId().getUsername() : getTranslation("notAvailable"))
                .setHeader(getTranslation("applicant"))
                .setSortable(true);

        projectGrid.addColumn(Project::getShortTitle)
                .setHeader(getTranslation("shortTitle"))
                .setSortable(true);

        projectGrid.addColumn(Project::getState)
                .setHeader(getTranslation("state"))
                .setSortable(true);

        projectGrid.addColumn(project -> project.getStartDate() != null ? project.getStartDate().toString() : getTranslation("notAvailable"))
                .setHeader(getTranslation("date"))
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
        dialog.setHeaderTitle(getTranslation("details.project"));

        dialog.setWidth("60%");

        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.getStyle().set("text-align", "center");

        // Title
        H2 title = new H2(project.getTitle());
        title.getStyle().set("margin-bottom", "10px");
        detailsLayout.add(title);

        // Details
        detailsLayout.add(createDetailField(getTranslation("title"), project.getTitle()));
        detailsLayout.add(createDetailField(getTranslation("promoter"), project.getPromoterId()));
        detailsLayout.add(createDetailField(getTranslation("applicant"),
                project.getApplicantId() != null ? project.getApplicantId().getUsername() : getTranslation("notAvailable")));
        detailsLayout.add(createDetailField(getTranslation("objective"), project.getScope()));
        detailsLayout.add(createDetailField(getTranslation("state"), project.getState()));
        detailsLayout.add(createDetailField(getTranslation("startDate"), project.getStartDate() != null ? project.getStartDate().toString() : getTranslation("cio.view.grid.notAvailable")));

        // File download
        if (project.getMemory() != null) {
            StreamResource resource = new StreamResource(
                    "memory.pdf",
                    () -> new ByteArrayInputStream(project.getMemory())
            );
            resource.setContentType("application/pdf");

            Button downloadButton = new Button(getTranslation("button.download.Memory"), e -> {
                Anchor downloadLink = new Anchor(resource, "");
                downloadLink.getElement().setAttribute("download", true);
                downloadLink.getStyle().set("display", "none");
                detailsLayout.getElement().appendChild(downloadLink.getElement());
                downloadLink.getElement().callJsFunction("click");
            });
            detailsLayout.add(downloadButton);
        } else {
            detailsLayout.add(createDetailField(getTranslation("memory"), getTranslation("details.noMemory")));
        }

        Button closeButton = new Button(getTranslation("button.close"), event -> dialog.close());
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

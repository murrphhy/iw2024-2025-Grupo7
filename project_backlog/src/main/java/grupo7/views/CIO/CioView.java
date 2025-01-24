package grupo7.views.CIO;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import grupo7.models.AppUser;
import grupo7.models.Project;
import grupo7.models.TechnicianProject;
import grupo7.models.keys.TechnicianProjectId;
import grupo7.repositories.TechnicianProjectRepository;
import grupo7.security.AuthenticatedUser;
import grupo7.services.EmailService;
import grupo7.services.ProjectService;
import grupo7.services.TechnicianProjectService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The {@code CioView} class represents the dashboard view for the Chief Information Officer (CIO).
 * It allows the CIO to view and prioritize projects that are in the "presentado" (presented) state.
 * The view includes functionalities to display project details, download associated files,
 * and update the strategic alignment of projects by calculating the average of six new evaluation fields.
 *
 * <p>This view is secured and only accessible to users with the "CIO" role.</p>
 */
@PageTitle("Evaluar Proyectos")
@Route("cio-dashboard")
@RolesAllowed("CIO")
@Menu(order = 3)
public class CioView extends VerticalLayout {

    private AuthenticatedUser authenticatedUser;
    private TechnicianProjectId technicianProjectId;
    private TechnicianProjectService technicianProjectService;
    private TechnicianProject technicianProject;
    private final ProjectService projectService;
    private final EmailService emailService;
    private final Grid<Project> projectGrid = new Grid<>(Project.class);
    private Grid<Project> evaluatedProjectsGrid = new Grid<>(Project.class, false);

    private final Binder<Project> binder = new Binder<>(Project.class);

    private final NumberField contribucionField = new NumberField(getTranslation("Contribución Estratégica"));
    private final NumberField valorEstudiantesField = new NumberField(getTranslation("Valor para el estudiantado"));
    private final NumberField viabilidadField = new NumberField(getTranslation("Viabilidad financiera "));
    private final NumberField riesgosField = new NumberField(getTranslation("Riesgos tecnológicos"));
    private final NumberField innovacionField = new NumberField(getTranslation("Innovación"));
    private final NumberField factibilidadField = new NumberField(getTranslation("Factibilidad"));

    /**
     * Constructs a new {@code CioView} instance with the specified services.
     *
     * @param projectService the service to manage projects
     * @param emailService   the service to handle email notifications
     */
    @Autowired
    public CioView(ProjectService projectService,
                   EmailService emailService,
                   AuthenticatedUser authenticatedUser,
                   TechnicianProjectService technicianProjectService,
                   TechnicianProjectRepository projectRepository) {

        this.projectService = projectService;
        this.emailService = emailService;
        this.authenticatedUser = authenticatedUser;
        this.technicianProjectService = technicianProjectService;

        setSizeFull();
        add(createProjectGrids());
        refreshGrid();
    }

    /**
     * Configura los campos de evaluación, asignándoles rango [0..10], paso 1 y anchura fija.
     */
    private void configureEvaluationFields() {
        for (NumberField nf : List.of(contribucionField, valorEstudiantesField, viabilidadField,
                riesgosField, innovacionField, factibilidadField)) {
            nf.setMin(0);
            nf.setMax(10);
            nf.setStep(1);
            nf.setWidth("250px");
        }
    }

    /**
     * Creates and configures the layout to display project grids, including
     * a grid for all projects and a separate grid for "Evaluated" projects.
     *
     * @return the configured {@code VerticalLayout} component
     */
    private VerticalLayout createProjectGrids() {
        List<Project> presentedProjects = projectService.getAllProjects().stream()
                .filter(project -> "Presentado".equalsIgnoreCase(project.getState()))
                .collect(Collectors.toList());

        projectGrid.removeAllColumns();
        projectGrid.addColumn(project -> project.getApplicantId() != null
                        ? project.getApplicantId().getUsername() : getTranslation("notAvailable"))
                .setHeader(getTranslation("applicant"))
                .setSortable(true);
        projectGrid.addColumn(Project::getShortTitle)
                .setHeader(getTranslation("shortTitle"))
                .setSortable(true);
        projectGrid.addColumn(Project::getState)
                .setHeader(getTranslation("state"))
                .setSortable(true);
        projectGrid.addColumn(project -> project.getStartDate() != null
                        ? project.getStartDate().toString() : getTranslation("notAvailable"))
                .setHeader(getTranslation("date"))
                .setSortable(true);

        projectGrid.asSingleSelect().addValueChangeListener(event -> editProject(event.getValue()));
        projectGrid.addItemDoubleClickListener(event -> openProjectDetailsDialog(event.getItem()));
        projectGrid.setItems(presentedProjects);

        evaluatedProjectsGrid = new Grid<>(Project.class, false);
        List<Project> evaluatedProjects = projectService.getAllProjects().stream()
                .filter(project -> "Evaluado".equalsIgnoreCase(project.getState()))
                .collect(Collectors.toList());

        evaluatedProjectsGrid.addColumn(project -> project.getApplicantId() != null
                        ? project.getApplicantId().getUsername() : getTranslation("notAvailable"))
                .setHeader(getTranslation("applicant"))
                .setSortable(true);
        evaluatedProjectsGrid.addColumn(Project::getShortTitle)
                .setHeader(getTranslation("shortTitle"))
                .setSortable(true);
        evaluatedProjectsGrid.addColumn(Project::getState)
                .setHeader(getTranslation("state"))
                .setSortable(true);
        evaluatedProjectsGrid.addColumn(project -> project.getStartDate() != null
                        ? project.getStartDate().toString() : getTranslation("notAvailable"))
                .setHeader(getTranslation("date"))
                .setSortable(true);

        evaluatedProjectsGrid.addItemDoubleClickListener(event -> showProjectDetailsDialog(event.getItem()));
        evaluatedProjectsGrid.setItems(evaluatedProjects);

        VerticalLayout layout = new VerticalLayout();
        layout.add(new H3(getTranslation("presentedProjects")), projectGrid);
        layout.add(new H3(getTranslation("evaluatedProjects")), evaluatedProjectsGrid);

        return layout;
    }

    /**
     * Logic for accepting a project.
     *
     * @param project the project to accept
     */
    private void acceptProject(Project project) {
        project.setState(projectService.getNextState(project.getState(), true));
        projectService.saveProject(project);
        Notification.show(getTranslation("projectAccepted") + ": " + project.getShortTitle());
    }

    /**
     * Logic for rejecting a project.
     *
     * @param project the project to reject
     */
    private void rejectProject(Project project) {
        project.setState(projectService.getNextState(project.getState(), false));
        projectService.saveProject(project);
        Notification.show(getTranslation("projectRejected") + ": " + project.getShortTitle());
    }

    /**
     * Opens a dialog showing the details of the selected project with "Accept" and "Reject" buttons.
     *
     * @param project the project to display in the dialog
     */
    private void showProjectDetailsDialog(Project project) {
        Dialog dialog = new Dialog();
        dialog.setWidth("80%");
        dialog.setHeight("80%");

        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.add(new H4(getTranslation("details.project")));
        detailsLayout.add(new Paragraph(getTranslation("applicant") + ": " +
                (project.getApplicantId() != null ? project.getApplicantId().getUsername() : getTranslation("notAvailable"))));
        detailsLayout.add(new Paragraph(getTranslation("shortTitle") + ": " + project.getShortTitle()));
        detailsLayout.add(new Paragraph(getTranslation("state") + ": " + project.getState()));
        detailsLayout.add(new Paragraph(getTranslation("date") + ": " +
                (project.getStartDate() != null ? project.getStartDate().toString() : getTranslation("notAvailable"))));

        Optional<AppUser> maybeUser = authenticatedUser.get();
        if (maybeUser.isEmpty()) {
            Notification.show(getTranslation("no.logged.in"));
            return;
        }

        technicianProjectId = new TechnicianProjectId(project.getApplicantId().getId(), project.getId());
        Optional<TechnicianProject> maybeProject = technicianProjectService.getTechnicianProjectById(technicianProjectId);

        if (maybeProject.isEmpty()) {
            Notification.show(getTranslation("noTechnicianProjectDetails"));
            return;
        }
        technicianProject = maybeProject.get();

        detailsLayout.add(new Paragraph(getTranslation("financialResources") + ": " +
                (technicianProject.getFinancialResources() != null
                        ? technicianProject.getFinancialResources().toString() : getTranslation("notAvailable"))));
        detailsLayout.add(new Paragraph(getTranslation("humanResources") + ": " +
                technicianProject.getHumanResources()));
        detailsLayout.add(new Paragraph(getTranslation("projectAppraisal") + ": " +
                (technicianProject.getProjectAppraisal() != null
                        ? technicianProject.getProjectAppraisal().toString() : getTranslation("notAvailable"))));

        Button acceptButton = new Button(getTranslation("accept"), event -> {
            acceptProject(project);
            dialog.close();
        });
        acceptButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button rejectButton = new Button(getTranslation("reject"), event -> {
            rejectProject(project);
            dialog.close();
        });
        rejectButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout buttonLayout = new HorizontalLayout(acceptButton, rejectButton);

        detailsLayout.add(buttonLayout);
        dialog.add(detailsLayout);
        dialog.open();
    }

    /**
     * Refreshes the data in the grids after changes.
     */
    private void refreshGrids() {
        projectGrid.setItems(projectService.getAllProjects().stream()
                .filter(project -> "Presentado".equalsIgnoreCase(project.getState()))
                .collect(Collectors.toList()));
        projectGrid.getDataProvider().refreshAll();

        evaluatedProjectsGrid.setItems(projectService.getAllProjects().stream()
                .filter(project -> "Evaluado".equalsIgnoreCase(project.getState()))
                .collect(Collectors.toList()));
        evaluatedProjectsGrid.getDataProvider().refreshAll();
    }

    /**
     * Edits the selected project by binding it to the form fields.
     *
     * @param project the selected {@code Project} to edit; {@code null} to clear the form
     */
    private void editProject(Project project) {
        if (project == null) {
            clearForm();
            return;
        }
        binder.setBean(project);
    }

    /**
     * Opens a dialog displaying detailed information about the specified project.
     * This includes project titles, promoters, applicants, objectives, state, start date,
     * and downloadable files for project regulations, technical specifications, and memory.
     * Additionally, it includes the six evaluation fields used to calculate the strategic alignment.
     *
     * @param project the {@code Project} whose details are to be displayed
     */
    private void openProjectDetailsDialog(Project project) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(getTranslation("details.project"));

        dialog.setWidth("80%");
        dialog.setHeight("80%");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSizeFull();
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.getStyle().set("text-align", "center");

        VerticalLayout scrollableContent = new VerticalLayout();
        scrollableContent.setSizeFull();
        scrollableContent.setSpacing(false);
        scrollableContent.setPadding(false);
        scrollableContent.getStyle().set("overflow", "auto");

        H2 title = new H2(project.getTitle());
        title.getStyle().set("margin-bottom", "10px");
        scrollableContent.add(title);

        scrollableContent.add(createDetailField(getTranslation("shortTitle"), project.getShortTitle()));
        scrollableContent.add(createDetailField(getTranslation("scope"), project.getScope()));

        String promoterDisplay = (project.getPromoterId() != null && !project.getPromoterId().isEmpty())
                ? project.getPromoterId()
                : getTranslation("noPromoter");
        scrollableContent.add(createDetailField(getTranslation("promoter"), promoterDisplay));

        scrollableContent.add(createDetailField(getTranslation("applicant"),
                project.getApplicantId() != null ? project.getApplicantId().getUsername() : getTranslation("notAvailable")));
        scrollableContent.add(createDetailField(getTranslation("state"), project.getState()));
        scrollableContent.add(createDetailField(getTranslation("startDate"),
                project.getStartDate() != null ? project.getStartDate().toString() : getTranslation("notAvailable")));

        // Archivos: memory, regulations, specs
        if (project.getMemory() != null && project.getMemory().length > 0) {
            scrollableContent.add(createDownloadField(getTranslation("button.download.Memory"), project.getMemory(),
                    project.getShortTitle() + "_memory.pdf"));
        } else {
            scrollableContent.add(createNoFileMessage(getTranslation("memory")));
        }

        if (project.getProjectRegulations() != null && project.getProjectRegulations().length > 0) {
            scrollableContent.add(createDownloadField(getTranslation("button.download.Regulations"),
                    project.getProjectRegulations(),
                    project.getShortTitle() + "_regulations.pdf"));
        } else {
            scrollableContent.add(createNoFileMessage(getTranslation("project.regulations")));
        }

        if (project.getTechnicalSpecifications() != null && project.getTechnicalSpecifications().length > 0) {
            scrollableContent.add(createDownloadField(getTranslation("button.download.Specifications"),
                    project.getTechnicalSpecifications(),
                    project.getShortTitle() + "_technical_specifications.pdf"));
        } else {
            scrollableContent.add(createNoFileMessage(getTranslation("technical.specifications")));
        }

        dialogLayout.add(scrollableContent);

        VerticalLayout evaluationForm = createEvaluationForm(project, dialog);
        dialogLayout.add(evaluationForm);

        dialog.add(dialogLayout);
        dialog.open();
    }

    /**
     * Creates a styled message indicating that no file has been uploaded for the specified label.
     *
     * @param label the label for which the file is missing
     * @return a {@code VerticalLayout} with the no-file message
     */
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
     * Creates the evaluation form with the six fields (0..10).
     * When saved, calculates the average and assigns it to the project's strategicAlignment.
     *
     * @param project the {@code Project} being evaluated
     * @param dialog  the {@code Dialog} instance to which this form belongs
     * @return the configured {@code VerticalLayout} containing the evaluation form
     */
    private VerticalLayout createEvaluationForm(Project project, Dialog dialog) {
        configureEvaluationFields();

        Button saveButton = new Button(getTranslation("button.save"), e -> {
            savePrioritization(project, dialog);
        });
        Button closeButton = new Button(getTranslation("button.close"), e -> dialog.close());

        HorizontalLayout row1 = new HorizontalLayout(contribucionField, valorEstudiantesField, viabilidadField);
        HorizontalLayout row2 = new HorizontalLayout(riesgosField, innovacionField, factibilidadField);

        VerticalLayout fieldsLayout = new VerticalLayout(row1, row2);

        HorizontalLayout formLayout = new HorizontalLayout(saveButton, closeButton);
        formLayout.setWidthFull();
        formLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        VerticalLayout evaluationForm = new VerticalLayout(fieldsLayout, formLayout);
        evaluationForm.setWidthFull();
        evaluationForm.getStyle().set("border-top", "1px solid #E0E0E0");
        evaluationForm.getStyle().set("background-color", "#F9F9F9");
        return evaluationForm;
    }

    /**
     * Saves the average of the six fields into project's strategicAlignment.
     * Then updates the project state, sends email if needed, etc.
     *
     * @param project the {@code Project} to prioritize
     * @param dialog  the {@code Dialog} instance to be closed after saving
     */
    private void savePrioritization(Project project, Dialog dialog) {
        if (project != null) {
            // Tomar valores y hacer la media
            double c1 = safeValue(contribucionField.getValue());
            double c2 = safeValue(valorEstudiantesField.getValue());
            double c3 = safeValue(viabilidadField.getValue());
            double c4 = safeValue(riesgosField.getValue());
            double c5 = safeValue(innovacionField.getValue());
            double c6 = safeValue(factibilidadField.getValue());

            double average = (c1 + c2 + c3 + c4 + c5 + c6) / 6.0;
            project.setStrategicAlignment(average);
            project.setState("Puntuado");

            AppUser applicant = project.getApplicantId();
            if (applicant != null) {
                String email = applicant.getEmail();
                String subject = getTranslation("email.subject");
                String message = String.format(
                        getTranslation("email.message"),
                        applicant.getUsername(),
                        project.getTitle(),
                        average
                );
                try {
                    emailService.sendEmail(email, subject, message);
                    Notification.show(getTranslation("notification.projectScored"),
                            3000, Notification.Position.BOTTOM_START);
                } catch (Exception e) {
                    e.printStackTrace();
                    Notification.show(getTranslation("notification.errorSendingEmail"),
                                    3000, Notification.Position.BOTTOM_START)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }

            projectService.saveProject(project);

            refreshGrid();
            dialog.close();
        }
    }

    /**
     * Helper para devolver 0.0 en lugar de null.
     */
    private double safeValue(Double val) {
        return (val == null) ? 0.0 : val;
    }

    /**
     * Clears the prioritization form by resetting the binder
     * and clearing the evaluation fields (if needed).
     */
    private void clearForm() {
        binder.setBean(null);
        contribucionField.clear();
        valorEstudiantesField.clear();
        viabilidadField.clear();
        riesgosField.clear();
        innovacionField.clear();
        factibilidadField.clear();
    }

    /**
     * Refreshes the project grid by fetching and displaying only the projects with the state "presentado".
     */
    private void refreshGrid() {
        List<Project> presentedProjects = projectService.getAllProjects().stream()
                .filter(project -> "Presentado".equalsIgnoreCase(project.getState()))
                .collect(Collectors.toList());
        projectGrid.setItems(presentedProjects);
    }

    /**
     * Creates a styled field with a label and a download link for binary files.
     * If the file content is not available, a message indicating the absence of the file is displayed.
     *
     * @param label       the label for the field
     * @param fileContent the binary content of the file
     * @param fileName    the name of the file for download purposes
     * @return a {@code VerticalLayout} containing the label and download link or a no-file message
     */
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

        if (fileContent != null && fileContent.length > 0) {
            StreamResource resource = new StreamResource(
                    fileName,
                    () -> new ByteArrayInputStream(fileContent)
            );
            resource.setContentType(determineContentType(fileName));

            Anchor downloadLink = new Anchor(resource, getTranslation("button.download") + label);
            downloadLink.getElement().setAttribute("download", true);
            downloadLink.getStyle().set("margin-top", "5px");
            downloadLink.getStyle().set("display", "inline-block");

            fieldLayout.add(labelComponent, downloadLink);
        } else {
            Paragraph noFileMessage = new Paragraph(getTranslation("no.file.uploaded"));
            noFileMessage.getStyle()
                    .set("font-size", "16px")
                    .set("font-weight", "normal")
                    .set("margin-top", "5px");
            fieldLayout.add(labelComponent, noFileMessage);
        }

        return fieldLayout;
    }

    /**
     * Determines the MIME type based on the file extension.
     *
     * @param fileName the name of the file
     * @return the corresponding MIME type as a string
     */
    private String determineContentType(String fileName) {
        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (fileName.endsWith(".txt")) {
            return "text/plain";
        } else {
            return "application/octet-stream";
        }
    }

    /**
     * Creates a styled field with a label and a textual value.
     *
     * @param label the label for the field
     * @param value the textual value of the field
     * @return a {@code VerticalLayout} containing the label and value
     */
    private VerticalLayout createDetailField(String label, String value) {
        VerticalLayout fieldLayout = new VerticalLayout();
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);

        Paragraph labelComponent = new Paragraph(label);
        labelComponent.getStyle()
                .set("color", "darkgray")
                .set("font-weight", "bold")
                .set("margin-bottom", "0")
                .set("text-align", "left");

        Paragraph valueComponent = new Paragraph(value);
        valueComponent.getStyle()
                .set("font-size", "16px")
                .set("font-weight", "normal")
                .set("margin-top", "0");

        fieldLayout.add(labelComponent, valueComponent);
        return fieldLayout;
    }
}

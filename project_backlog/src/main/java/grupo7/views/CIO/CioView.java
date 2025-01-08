package grupo7.views.CIO;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
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

import grupo7.models.Project;
import grupo7.models.AppUser;
import grupo7.services.ProjectService;
import grupo7.services.EmailService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code CioView} class represents the dashboard view for the Chief Information Officer (CIO).
 * It allows the CIO to view and prioritize projects that are in the "presentado" (presented) state.
 * The view includes functionalities to display project details, download associated files,
 * and update the strategic alignment of projects.
 *
 * <p>This view is secured and only accessible to users with the "CIO" role.</p>
 */
@PageTitle("Cio View")
@Route("cio-dashboard")
@RolesAllowed("CIO")
@Menu(order = 3)
public class CioView extends VerticalLayout {

    private final ProjectService projectService;
    private final EmailService emailService;
    private final Grid<Project> projectGrid = new Grid<>(Project.class);
    private final Binder<Project> binder = new Binder<>(Project.class);

    /**
     * NumberField for entering strategic alignment value.
     */
    private final NumberField strategicAlignmentField = new NumberField(getTranslation("alignment"));

    /**
     * Constructs a new {@code CioView} instance with the specified services.
     *
     * @param projectService the service to manage projects
     * @param emailService   the service to handle email notifications
     */
    @Autowired
    public CioView(ProjectService projectService, EmailService emailService) {
        this.projectService = projectService;
        this.emailService = emailService;

        binder.forField(strategicAlignmentField)
                .bind(Project::getStrategicAlignment, Project::setStrategicAlignment);

        setSizeFull();
        add(createProjectGrid());
        refreshGrid();
    }

    /**
     * Creates and configures the project grid to display relevant project information.
     *
     * @return the configured {@code Grid<Project>} component
     */
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
     * Additionally, it includes a strategic alignment evaluation form that is always visible at the bottom.
     *
     * @param project the {@code Project} whose details are to be displayed
     */
    private void openProjectDetailsDialog(Project project) {
        // Create a new dialog instance
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

        if (project.getProjectRegulations() != null && project.getProjectRegulations().length > 0) {
            scrollableContent.add(createDownloadField(getTranslation("button.download.Regulations"), project.getProjectRegulations(),
                    project.getShortTitle() + "_regulations.pdf"));
        } else {
            scrollableContent.add(createNoFileMessage(getTranslation("no.file.uploaded")));
        }

        if (project.getTechnicalSpecifications() != null && project.getTechnicalSpecifications().length > 0) {
            scrollableContent.add(createDownloadField(getTranslation("button.download.Specifications"), project.getTechnicalSpecifications(),
                    project.getShortTitle() + "_technical_specifications.pdf"));
        } else {
            scrollableContent.add(createNoFileMessage(getTranslation("no.file.uploaded")));
        }

        if (project.getMemory() != null && project.getMemory().length > 0) {
            scrollableContent.add(createDownloadField(getTranslation("button.download.Memory"), project.getMemory(),
                    project.getShortTitle() + "_memory.pdf"));
        } else {
            scrollableContent.add(createNoFileMessage(getTranslation("no.file.uploaded")));
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
     * @return a {@code Paragraph} component with the no-file message
     */
    private Paragraph createNoFileMessage(String label) {
        Paragraph noFileMessage = new Paragraph(getTranslation("no.file.uploaded") + "para " + label + ".");
        noFileMessage.getStyle()
                .set("font-size", "16px")
                .set("font-weight", "normal")
                .set("margin-top", "5px")
                .set("color", "darkred");
        return noFileMessage;
    }

    /**
     * Creates the strategic alignment evaluation form consisting of a number field and a save button.
     * This form is designed to always remain visible at the bottom of the dialog.
     *
     * @param project the {@code Project} being evaluated
     * @param dialog  the {@code Dialog} instance to which this form belongs
     * @return the configured {@code VerticalLayout} containing the evaluation form
     */
    private VerticalLayout createEvaluationForm(Project project, Dialog dialog) {
        strategicAlignmentField.setValue(project.getStrategicAlignment() != null ? project.getStrategicAlignment() : 0.0);

        Button saveButton = new Button(getTranslation("button.save"), event -> {
            savePrioritization(project, dialog);
        });

        HorizontalLayout formLayout = new HorizontalLayout(
                strategicAlignmentField,
                saveButton
        );
        formLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        formLayout.setWidthFull();

        VerticalLayout evaluationForm = new VerticalLayout(formLayout);
        evaluationForm.setWidthFull();
        evaluationForm.setPadding(true);
        evaluationForm.setSpacing(true);
        evaluationForm.getStyle().set("border-top", "1px solid #E0E0E0");
        evaluationForm.getStyle().set("background-color", "#F9F9F9");

        return evaluationForm;
    }

    /**
     * Saves the strategic alignment prioritization for the specified project.
     * Updates the project's state and sends an email notification to the applicant if applicable.
     *
     * @param project the {@code Project} to prioritize
     * @param dialog  the {@code Dialog} instance to be closed after saving
     */
    private void savePrioritization(Project project, Dialog dialog) {
        if (project != null) {
            Double alignmentValue = strategicAlignmentField.getValue();
            if (alignmentValue != null) {
                project.setStrategicAlignment(alignmentValue);
                project.setState("Puntuado");

                AppUser applicant = project.getApplicantId();

                if (applicant != null) {
                    String email = applicant.getEmail();
                    String subject = getTranslation("email.subject"); // Traducción del asunto
                    String message = String.format(
                            getTranslation("email.message"), // Traducción del cuerpo del mensaje
                            applicant.getUsername(),
                            project.getTitle(),
                            alignmentValue
                    );
                
                    try {
                        emailService.sendEmail(email, subject, message);
                        Notification.show(getTranslation("notification.projectScored"), 3000, Notification.Position.BOTTOM_START);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Notification.show(getTranslation("notification.errorSendingEmail"), 3000, Notification.Position.BOTTOM_START)
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                }                

                projectService.saveProject(project);

                refreshGrid();
                dialog.close();
            } else {
                Notification.show(getTranslation("notification.missingStrategicAlignment"), 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_WARNING);
            }
        }
    }

    /**
     * Clears the prioritization form by resetting the binder and clearing the strategic alignment field.
     */
    private void clearForm() {
        binder.setBean(null);
        strategicAlignmentField.clear();
    }

    /**
     * Refreshes the project grid by fetching and displaying only the projects with the state "presentado".
     */
    private void refreshGrid() {
        List<Project> presentedProjects = projectService.getAllProjects().stream()
                .filter(project -> getTranslation("project.state.presented").equalsIgnoreCase(project.getState()))
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
            // Create a stream resource for the file
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
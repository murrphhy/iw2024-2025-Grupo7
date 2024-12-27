package grupo7.views.home;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import grupo7.models.Project;
import grupo7.models.AppUser;
import grupo7.security.AuthenticatedUser;
import grupo7.services.ProjectService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
// or your own AuthenticatedUser / SecurityUtils (see below)

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@PageTitle("Projects")
@Route("")
@Menu(order = 1, icon = "line-awesome/svg/file.svg")
@PermitAll
public class Home extends VerticalLayout {

    private final ProjectService projectService;
    private final Grid<Project> projectGrid;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    @Autowired
    public Home(ProjectService projectService) {
        this.projectService = projectService;
        this.projectGrid = new Grid<>(Project.class, false);

        Button addProjectButton = new Button("New Project", e -> openNewProjectDialog());

        HorizontalLayout topBar = new HorizontalLayout(addProjectButton);
        add(topBar);

        configureGrid();
        add(projectGrid);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    private void configureGrid() {
        projectGrid.addColumn(Project::getId).setHeader("ID");
        projectGrid.addColumn(project ->
                project.getApplicantId() != null
                        ? project.getApplicantId().getUsername()
                        : "N/A"
        ).setHeader("Applicant");
        projectGrid.addColumn(Project::getPromoterId).setHeader("Promoter ID");
        projectGrid.addColumn(Project::getTitle).setHeader("Title");
        projectGrid.addColumn(Project::getShortTitle).setHeader("Short Title");
        projectGrid.addColumn(Project::getScope).setHeader("Scope");

        // We won't show 'state' because it's defaulting to "presentado"
        // If you want a column to see it in the grid, you can re-add:
        // projectGrid.addColumn(Project::getState).setHeader("State");

        // Date column
        projectGrid.addColumn(project -> {
            Date d = project.getStartDate();
            return d != null ? d.toString() : "";
        }).setHeader("Start Date");

        // Memory excerpt
        projectGrid.addColumn(project -> {
            String mem = project.getMemory();
            if (mem == null) return "";
            return mem.length() > 30 ? mem.substring(0, 30) + "..." : mem;
        }).setHeader("Memory Excerpt");

        projectGrid.setItems(projectService.getAllProjects());
        projectGrid.setWidth("90%");
        projectGrid.setHeight("400px");
    }

    private void openNewProjectDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("New Project");

        FormLayout formLayout = new FormLayout();

        TextField titleField = new TextField("Title");
        TextField shortTitleField = new TextField("Short Title");
        TextField scopeField = new TextField("Scope");
        TextField promoterField = new TextField("Promoter ID");
        TextField memoryField = new TextField("Memory");
        DatePicker startDatePicker = new DatePicker("Start Date");

        formLayout.add(
                titleField,
                shortTitleField,
                scopeField,
                promoterField,
                memoryField,
                startDatePicker
        );

        // "Save" and "Cancel" buttons
        Button saveButton = new Button("Save", event -> {
            Project newProject = new Project();
            newProject.setTitle(titleField.getValue());
            newProject.setShortTitle(shortTitleField.getValue());
            newProject.setScope(scopeField.getValue());
            newProject.setMemory(memoryField.getValue());

            if (!promoterField.getValue().isEmpty()) {
                try {
                    Long promoterId = Long.valueOf(promoterField.getValue());
                    newProject.setPromoterId(promoterId);
                } catch (NumberFormatException ex) {
                    newProject.setPromoterId(null);
                }
            }

            LocalDate localDate = startDatePicker.getValue();
            if (localDate != null) {
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                newProject.setStartDate(date);
            }

            newProject.setState("presentado");

            Optional<AppUser> maybeUser = authenticatedUser.get();
            if (maybeUser.isEmpty()) {
                Notification.show("No hay usuario logueado. Por favor, inicia sesión.");
                return;
            }
            AppUser currentUser = maybeUser.get();
            newProject.setApplicantId(currentUser);

            projectService.saveProject(newProject);

            projectGrid.setItems(projectService.getAllProjects());

            dialog.close();
        });

        Button cancelButton = new Button("Cancel", event -> dialog.close());
        HorizontalLayout footerLayout = new HorizontalLayout(saveButton, cancelButton);

        dialog.add(formLayout, footerLayout);
        dialog.open();
    }

}

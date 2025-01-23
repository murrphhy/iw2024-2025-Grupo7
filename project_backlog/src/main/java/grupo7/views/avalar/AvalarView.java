package grupo7.views.avalar;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import grupo7.models.Project;
import grupo7.models.AppUser;
import grupo7.models.Role;
import grupo7.security.AuthenticatedUser;
import grupo7.services.ProjectService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@PageTitle("Avalar")
@Route("avalar")
@Menu(order = 4)
@RolesAllowed("PROMOTER")
public class AvalarView extends VerticalLayout {

    private final ProjectService projectService;
    private final Grid<Project> projectGrid;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    @Autowired
    public AvalarView(ProjectService projectService, AuthenticatedUser authenticatedUser) {
        this.projectService = projectService;
        this.authenticatedUser = authenticatedUser;
        this.projectGrid = new Grid<>(Project.class, false);

        Optional<AppUser> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            AppUser currentUser = maybeUser.get();
            if (currentUser.getRole() == Role.PROMOTER) {
                configureGrid(currentUser.getUsername());
                add(projectGrid);

                setSizeFull();
                setJustifyContentMode(JustifyContentMode.CENTER);
                setDefaultHorizontalComponentAlignment(Alignment.CENTER);
                getStyle().set("text-align", "center");
            } else {
                Notification.show("Acceso denegado. Solo para promotores.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            Notification.show("Usuario no autenticado.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void configureGrid(String promoterUsername) {
        projectGrid.removeAllColumns();

        projectGrid.addColumn(project ->
                project.getApplicantId() != null
                        ? project.getApplicantId().getUsername()
                        : "No disponible"
        ).setHeader("Solicitante");

        projectGrid.addColumn(Project::getShortTitle).setHeader("Título Corto");

        projectGrid.addColumn(Project::getState).setHeader("Estado");

        projectGrid.addColumn(project -> {
            if (project.getStartDate() != null) {
                return project.getStartDate().toString();
            } else {
                return "No disponible";
            }
        }).setHeader("Fecha de Inicio").setTextAlign(ColumnTextAlign.END);

        projectGrid.addComponentColumn(this::createAvalarButton).setHeader("Acciones");

        List<Project> projectsToDisplay = projectService.getAllProjects().stream()
                .filter(project -> "esperando aval".equals(project.getState()))
                .filter(project -> promoterUsername.equals(project.getPromoterId()))
                .collect(Collectors.toList());

        projectGrid.setItems(projectsToDisplay);
        projectGrid.setWidth("90%");
        projectGrid.setHeight("400px");

        projectGrid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::openProjectDetailsDialog));
    }

    private Button createAvalarButton(Project project) {
        Button avalarButton = new Button("Avalar", event -> showConfirmDialog(project));
        avalarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return avalarButton;
    }

    private void showConfirmDialog(Project project) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirmar Avalar Proyecto");

        Paragraph confirmationText = new Paragraph("¿Estás seguro de que deseas avalar el proyecto \"" + project.getShortTitle() + "\"?");
        confirmDialog.add(confirmationText);

        Button confirmButton = new Button("Confirmar", event -> {
            avalarProject(project);
            confirmDialog.close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancelar", event -> confirmDialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout footer = new HorizontalLayout(confirmButton, cancelButton);
        footer.setJustifyContentMode(JustifyContentMode.END);
        confirmDialog.add(footer);

        confirmDialog.open();
    }

    private void avalarProject(Project project) {
        project.setState("avalado");
        projectService.saveProject(project);
        projectGrid.getDataProvider().refreshItem(project);
        Notification.show("Proyecto avalado correctamente.", 3000, Notification.Position.BOTTOM_START)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        refreshGrid();
    }


    private void openProjectDetailsDialog(Project project) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Detalles del Proyecto");

        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.getStyle().set("text-align", "center");

        detailsLayout.add(new Paragraph("Título: " + project.getTitle()));
        detailsLayout.add(new Paragraph("Título Corto: " + project.getShortTitle()));
        detailsLayout.add(new Paragraph("Solicitante: " + (project.getApplicantId() != null ? project.getApplicantId().getUsername() : "No disponible")));
        detailsLayout.add(new Paragraph("Estado: " + project.getState()));
        detailsLayout.add(new Paragraph("Fecha de Inicio: " + (project.getStartDate() != null ? project.getStartDate().toString() : "No disponible")));

        if (project.getMemory() != null) {
            detailsLayout.add(createDownloadLink("Memoria", project.getMemory(), project.getShortTitle() + "_memory.pdf"));
        }
        if (project.getProjectRegulations() != null) {
            detailsLayout.add(createDownloadLink("Regulaciones del Proyecto", project.getProjectRegulations(), project.getShortTitle() + "_regulations.pdf"));
        }
        if (project.getTechnicalSpecifications() != null) {
            detailsLayout.add(createDownloadLink("Especificaciones Técnicas", project.getTechnicalSpecifications(), project.getShortTitle() + "_specifications.pdf"));
        }

        Button closeButton = new Button("Cerrar", event -> dialog.close());
        HorizontalLayout footer = new HorizontalLayout(closeButton);
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.END);

        dialog.add(detailsLayout, footer);
        dialog.open();
    }

    private Anchor createDownloadLink(String label, byte[] fileContent, String fileName) {
        StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(fileContent));
        resource.setContentType("application/pdf");

        Anchor downloadLink = new Anchor(resource, "Descargar " + label);
        downloadLink.getElement().setAttribute("download", true);
        return downloadLink;
    }

    /**
     * Refreshes the project grid by fetching and displaying only the projects with the state "esperando aval".
     */
    private void refreshGrid() {
        List<Project> alignedProjects = projectService.getAllProjects().stream()
                .filter(project -> "esperando aval".equalsIgnoreCase(project.getState()))
                .collect(Collectors.toList());
        projectGrid.setItems(alignedProjects);
    }
}

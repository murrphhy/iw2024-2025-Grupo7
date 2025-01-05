package grupo7.views.home;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import grupo7.models.Project;
import grupo7.models.AppUser;
import grupo7.models.Promoter;
import grupo7.security.AuthenticatedUser;
import grupo7.services.ProjectService;
import grupo7.services.PromoterApiResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@PageTitle("Home")
@Route("")
@Menu(order = 1)
@AnonymousAllowed
public class Home extends VerticalLayout {

    private final ProjectService projectService;
    private final Grid<Project> projectGrid;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    @Autowired
    public Home(ProjectService projectService, AuthenticatedUser authenticatedUser) {
        this.projectService = projectService;
        this.authenticatedUser = authenticatedUser;
        this.projectGrid = new Grid<>(Project.class, false);

        if (authenticatedUser != null && authenticatedUser.get().isPresent()) {
            Button addProjectButton = new Button("New Project", e -> openNewProjectDialog());
            HorizontalLayout topBar = new HorizontalLayout(addProjectButton);
            add(topBar);
        }

        configureGrid();
        add(projectGrid);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    private void configureGrid() {
        projectGrid.removeAllColumns(); // Elimina cualquier columna previa.

        // Configurar columnas específicas
        projectGrid.addColumn(project ->
                project.getApplicantId() != null
                        ? project.getApplicantId().getUsername()
                        : "N/A"
        ).setHeader("Solicitante");

        projectGrid.addColumn(Project::getPromoterId).setHeader("Promotor");

        projectGrid.addColumn(Project::getShortTitle).setHeader("Título Corto");

        projectGrid.addColumn(Project::getState).setHeader("Estado");

        projectGrid.addColumn(project -> {
            Date d = project.getStartDate();
            return d != null ? d.toString() : "N/A";
        }).setHeader("Fecha").setTextAlign(ColumnTextAlign.END); // Alineación a la derecha.

        projectGrid.setItems(projectService.getAllProjects());
        projectGrid.setWidth("90%");
        projectGrid.setHeight("400px");

        // Agregar listener de selección para los botones.
        projectGrid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::openProjectDetailsDialog));
    }

    private void openProjectDetailsDialog(Project project) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Project Details");

        dialog.setWidth("60%");

        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.getStyle().set("text-align", "center");

        // Título principal
        H2 title = new H2(project.getTitle());
        title.getStyle().set("margin-bottom", "10px");
        detailsLayout.add(title);

        // Detalles estilizados
        detailsLayout.add(createDetailField("Short Title", project.getShortTitle()));
        detailsLayout.add(createDetailField("Scope", project.getScope()));
        detailsLayout.add(createDetailField("Promoter", project.getPromoterId()));
        detailsLayout.add(createDetailField("Applicant",
                project.getApplicantId() != null ? project.getApplicantId().getUsername() : "N/A"));
        detailsLayout.add(createDetailField("State", project.getState()));
        detailsLayout.add(createDetailField("Start Date", project.getStartDate() != null ? project.getStartDate().toString() : "N/A"));

        // Descargar archivo de memoria si existe
        if (project.getMemory() != null) {
            StreamResource resource = new StreamResource(
                    "memory.pdf",
                    () -> new ByteArrayInputStream(project.getMemory())
            );
            resource.setContentType("application/pdf");

            Button downloadButton = new Button("Download Memory", e -> {
                Anchor downloadLink = new Anchor(resource, "");
                downloadLink.getElement().setAttribute("download", true);
                downloadLink.getStyle().set("display", "none");
                detailsLayout.getElement().appendChild(downloadLink.getElement());
                downloadLink.getElement().callJsFunction("click");
            });
            detailsLayout.add(downloadButton);
        } else {
            detailsLayout.add(createDetailField("Memory", "No memory file uploaded."));
        }

        Button closeButton = new Button("Close", event -> dialog.close());
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






    private void openNewProjectDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("New Project");

        FormLayout formLayout = new FormLayout();

        TextField titleField = new TextField("Title");
        TextField shortTitleField = new TextField("Short Title");
        TextField scopeField = new TextField("Scope");
        DatePicker startDatePicker = new DatePicker("Start Date");

        ComboBox<Promoter> promoterComboBox = new ComboBox<>("Promoter");
        promoterComboBox.setPlaceholder("Select a promoter");
        loadPromoters(promoterComboBox);

        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload memoryUpload = new Upload(memoryBuffer);
        memoryUpload.setWidthFull();
        memoryUpload.setHeight("200px");
        memoryUpload.setAcceptedFileTypes(".pdf", ".docx", ".txt");
        memoryUpload.setUploadButton(new Button("Upload Memory"));
        memoryUpload.setDropLabel(new Paragraph("Drop memory file here or click to upload."));

        formLayout.add(titleField, shortTitleField, scopeField, startDatePicker, promoterComboBox, memoryUpload);

        Button saveButton = new Button("Save", event -> {
            Project newProject = new Project();
            newProject.setTitle(titleField.getValue());
            newProject.setShortTitle(shortTitleField.getValue());
            newProject.setScope(scopeField.getValue());

            LocalDate localDate = startDatePicker.getValue();
            if (localDate != null) {
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                newProject.setStartDate(date);
            }

            newProject.setState("Solicitado");

            Promoter selectedPromoter = promoterComboBox.getValue();
            if (selectedPromoter != null) {
                newProject.setPromoterId(selectedPromoter.getNombre());
            } else {
                Notification.show("Please select a promoter.");
                return;
            }

            Optional<AppUser> maybeUser = authenticatedUser.get();
            if (maybeUser.isEmpty()) {
                Notification.show("No user logged in. Please log in.");
                return;
            }
            AppUser currentUser = maybeUser.get();
            newProject.setApplicantId(currentUser);

            if (memoryBuffer.getInputStream() != null) {
                try {
                    byte[] memoryContent = memoryBuffer.getInputStream().readAllBytes();
                    newProject.setMemory(memoryContent);
                } catch (Exception e) {
                    Notification.show("Failed to upload memory file.");
                    e.printStackTrace();
                    return;
                }
            } else {
                Notification.show("Please upload a memory file.");
                return;
            }

            projectService.saveProject(newProject);
            projectGrid.setItems(projectService.getAllProjects());
            dialog.close();
        });

        Button cancelButton = new Button("Cancel", event -> dialog.close());
        HorizontalLayout footerLayout = new HorizontalLayout(saveButton, cancelButton);

        dialog.add(formLayout, footerLayout);
        dialog.open();
    }

    private static List<Promoter> cachedPromoters = null;
    private static long cacheTimestamp = 0;
    private static final long CACHE_DURATION_MS = 5 * 24 * 60 * 60 * 1000L;

    private void loadPromoters(ComboBox<Promoter> promoterComboBox) {
        try {
            if (cachedPromoters == null || (System.currentTimeMillis() - cacheTimestamp) > CACHE_DURATION_MS) {
                URL url = new URL("https://e608f590-1a0b-43c5-b363-e5a883961765.mock.pstmn.io/sponsors");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                ObjectMapper mapper = new ObjectMapper();

                PromoterApiResponse response = mapper.readValue(conn.getInputStream(), PromoterApiResponse.class);
                cachedPromoters = response.getData();
                cacheTimestamp = System.currentTimeMillis();

                conn.disconnect();
            }

            promoterComboBox.setItems(cachedPromoters);
            promoterComboBox.setItemLabelGenerator(Promoter::getDisplayName);
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Failed to load promoters.");
        }
    }
}

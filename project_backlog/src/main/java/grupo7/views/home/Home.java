package grupo7.views.home;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import grupo7.models.Project;
import grupo7.models.AppUser;
import grupo7.models.Promoter;
import grupo7.security.AuthenticatedUser;
import grupo7.services.ProjectService;
import grupo7.services.PromoterApiResponse;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
// or your own AuthenticatedUser / SecurityUtils (see below)

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@PageTitle("Projects")
@Route("")
@Menu(order = 1, icon = "line-awesome/svg/file.svg")
@AnonymousAllowed
public class Home extends VerticalLayout {

    private final ProjectService projectService;
    private final Grid<Project> projectGrid;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    @Autowired
    public Home(ProjectService projectService, AuthenticatedUser authenticatedUser) {
        this.projectService = projectService;
        this.authenticatedUser = authenticatedUser; // Puede ser null
        this.projectGrid = new Grid<>(Project.class, false);

        if (authenticatedUser != null && authenticatedUser.get().isPresent()) {
            // Si el usuario está autenticado, muestra el botón
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
        projectGrid.addColumn(project ->
                project.getApplicantId() != null
                        ? project.getApplicantId().getUsername()
                        : "N/A"
        ).setHeader("Applicant");
        projectGrid.addColumn(Project::getPromoterId).setHeader("Promoter");
        projectGrid.addColumn(Project::getTitle).setHeader("Title");
        projectGrid.addColumn(Project::getShortTitle).setHeader("Short Title");
        projectGrid.addColumn(Project::getScope).setHeader("Scope");

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
        DatePicker startDatePicker = new DatePicker("Start Date");

        // ComboBox para seleccionar promotores
        ComboBox<Promoter> promoterComboBox = new ComboBox<>("Promoter");
        promoterComboBox.setPlaceholder("Select a promoter");
        loadPromoters(promoterComboBox); // Cargar datos desde la API o caché

        // Campo de carga de archivos para "Memory"
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload memoryUpload = new Upload(memoryBuffer);
        memoryUpload.setWidthFull();
        memoryUpload.setHeight("200px");
        memoryUpload.setAcceptedFileTypes(".pdf", ".docx", ".txt"); // Tipos de archivo permitidos
        memoryUpload.setUploadButton(new Button("Upload Memory"));
        memoryUpload.setDropLabel(new Paragraph("Drop memory file here or click to upload."));

        formLayout.add(
                titleField,
                shortTitleField,
                scopeField,
                startDatePicker,
                promoterComboBox,
                memoryUpload
        );

        // Botones "Save" y "Cancel"
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

            newProject.setState("presentado");

            // Obtener el promotor seleccionado
            Promoter selectedPromoter = promoterComboBox.getValue();
            if (selectedPromoter != null) {
                newProject.setPromoterId(selectedPromoter.getNombre());
            } else {
                Notification.show("Please select a promoter.");
                return;
            }

            // Obtener el usuario autenticado
            Optional<AppUser> maybeUser = authenticatedUser.get();
            if (maybeUser.isEmpty()) {
                Notification.show("No user logged in. Please log in.");
                return;
            }
            AppUser currentUser = maybeUser.get();
            newProject.setApplicantId(currentUser);

            // Validar y guardar el archivo de "Memory"
            if (memoryBuffer.getInputStream() != null) {
                try {
                    byte[] memoryContent = memoryBuffer.getInputStream().readAllBytes();
                    String memoryFileName = memoryBuffer.getFileName();
                    // Aquí puedes guardar el archivo en tu base de datos o sistema de archivos
                    newProject.setMemory(memoryFileName); // Guarda el nombre del archivo como referencia
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
    private static final long CACHE_DURATION_MS = 5 * 24 * 60 * 60 * 1000L; // 5 días en milisegundos

    private void loadPromoters(ComboBox<Promoter> promoterComboBox) {
        try {
            // Verifica si la caché está vacía o ha expirado
            if (cachedPromoters == null || (System.currentTimeMillis() - cacheTimestamp) > CACHE_DURATION_MS) {
                URL url = new URL("https://e608f590-1a0b-43c5-b363-e5a883961765.mock.pstmn.io/sponsors");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                ObjectMapper mapper = new ObjectMapper();

                // Deserializar la respuesta JSON a PromoterApiResponse
                PromoterApiResponse response = mapper.readValue(conn.getInputStream(), PromoterApiResponse.class);
                cachedPromoters = response.getData(); // Extraer la lista de promotores
                cacheTimestamp = System.currentTimeMillis(); // Actualizar el timestamp del caché

                conn.disconnect();
                System.out.println("Promoters loaded and cached: " + cachedPromoters);
            } else {
                System.out.println("Using cached promoters: " + cachedPromoters);
            }

            promoterComboBox.setItems(cachedPromoters);
            promoterComboBox.setItemLabelGenerator(Promoter::getDisplayName);
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Failed to load promoters.");
        }
    }

}
package grupo7.views.home;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
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
import grupo7.models.Role;
import grupo7.security.AuthenticatedUser;
import grupo7.services.ProjectService;
import grupo7.services.PromoterApiResponse;
import grupo7.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@PageTitle("Home")
@Route("")
@Menu(order = 1)
@AnonymousAllowed
public class Home extends VerticalLayout {

    private final ProjectService projectService;
    private final Grid<Project> projectGrid;
    private final UserService userService;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    // ComboBox de promotores cargado al iniciar
    private ComboBox<AppUser> promoterComboBox = new ComboBox<>("Promotor");

    @Autowired
    public Home(ProjectService projectService, AuthenticatedUser authenticatedUser, UserService userService) {
        this.projectService = projectService;
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.projectGrid = new Grid<>(Project.class, false);

        if (authenticatedUser != null && authenticatedUser.get().isPresent()) {
            Button addProjectButton = new Button("Nuevo Proyecto", e -> openNewProjectDialog());
            HorizontalLayout topBar = new HorizontalLayout(addProjectButton);
            add(topBar);
        }

        configureGrid();
        add(projectGrid);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        // Cargar promotores al iniciar la vista
        loadPromotersOnStartup();
    }

    private void configureGrid() {
        projectGrid.removeAllColumns(); // Remove any existing columns

        // Configure specific columns
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
        }).setHeader("Fecha").setTextAlign(ColumnTextAlign.END); // Right alignment

        Optional<AppUser> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            AppUser currentUser = maybeUser.get();
            if (currentUser.getRole() == Role.PROMOTER) {
                projectGrid.addComponentColumn(project -> {
                    if (project.getPromoterId() == null) {
                        Button assignButton = new Button("Avalar", e -> assignPromoter(project));
                        assignButton.setIcon(VaadinIcon.USER_CHECK.create());
                        assignButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                        return assignButton;
                    } else {
                        return new Button();
                    }
                }).setHeader("Acciones").setWidth("180px").setFlexGrow(0);
            }
        }


        projectGrid.setItems(projectService.getAllProjects());
        projectGrid.setWidth("90%");
        projectGrid.setHeight("400px");

        // Add selection listener to open project details dialog
        projectGrid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::openProjectDetailsDialog));
    }

    /**
     * Metodo para asignar el promoterId al proyecto.
     * Asigna el promoterId del usuario actual al proyecto y actualiza la cuadrícula.
     *
     * @param project El proyecto al que se asignará el promoterId.
     */
    private void assignPromoter(Project project) {
        Optional<AppUser> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            AppUser currentUser = maybeUser.get();
            if (currentUser.getRole() == Role.PROMOTER) {
                // Asignar el promoterId al username del promotor actual
                project.setPromoterId(currentUser.getUsername()); // Asegúrate de que promoterId es de tipo String
                projectService.saveProject(project); // Guardar los cambios
                Notification.show("Has asignado tu ID como promotor al proyecto.", 3000, Notification.Position.BOTTOM_START);
                projectGrid.getDataProvider().refreshItem(project); // Actualizar solo el ítem modificado
            } else {
                Notification.show("No tienes permisos para avalar proyectos.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            Notification.show("No hay ningún usuario logueado.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }


    private void openProjectDetailsDialog(Project project) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Detalles del Proyecto");

        dialog.setWidth("60%");

        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.getStyle().set("text-align", "center");

        // Main title
        H2 title = new H2(project.getTitle());
        title.getStyle().set("margin-bottom", "10px");
        detailsLayout.add(title);

        // Styled details
        detailsLayout.add(createDetailField("Título Corto", project.getShortTitle()));
        detailsLayout.add(createDetailField("Alcance", project.getScope()));
        String promoterDisplay = (project.getPromoterId() != null && !project.getPromoterId().isEmpty())
                ? project.getPromoterId()
                : "Sin promotor";
        detailsLayout.add(createDetailField("Promotor", promoterDisplay));
        detailsLayout.add(createDetailField("Solicitante",
                project.getApplicantId() != null ? project.getApplicantId().getUsername() : "N/A"));
        detailsLayout.add(createDetailField("Estado", project.getState()));
        detailsLayout.add(createDetailField("Fecha de Inicio",
                project.getStartDate() != null ? project.getStartDate().toString() : "N/A"));

        // Download Memory file if exists
        detailsLayout.add(createDownloadField("Memoria", project.getMemory(), project.getShortTitle() + "_memory.pdf"));

        // Add buttons for Project Regulations and Technical Specifications
        if (project.getProjectRegulations() != null && project.getProjectRegulations().length > 0) {
            detailsLayout.add(createDownloadField("Regulaciones del Proyecto", project.getProjectRegulations(), project.getShortTitle() + "_project_regulations.pdf"));
        } else {
            detailsLayout.add(createNoFileMessage("Regulaciones del Proyecto"));
        }

        if (project.getTechnicalSpecifications() != null && project.getTechnicalSpecifications().length > 0) {
            detailsLayout.add(createDownloadField("Especificaciones Técnicas", project.getTechnicalSpecifications(), project.getShortTitle() + "_technical_specifications.pdf"));
        } else {
            detailsLayout.add(createNoFileMessage("Especificaciones Técnicas"));
        }

        Button closeButton = new Button("Cerrar", event -> dialog.close());
        HorizontalLayout footer = new HorizontalLayout(closeButton);
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.END);

        dialog.add(detailsLayout, footer);
        dialog.open();
    }

    /**
     * Crea un campo estilizado con etiqueta y enlace de descarga o mensaje.
     *
     * @param label         La etiqueta del campo.
     * @param fileContent   El contenido del archivo en bytes.
     * @param fileName      El nombre del archivo para la descarga.
     * @return Un VerticalLayout con la etiqueta y el enlace de descarga o mensaje.
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

        StreamResource resource = new StreamResource(
                fileName,
                () -> new ByteArrayInputStream(fileContent)
        );
        resource.setContentType("application/pdf"); // Adjust the type according to the file

        Anchor downloadLink = new Anchor(resource, "Descargar " + label);
        downloadLink.getElement().setAttribute("download", true);
        downloadLink.getStyle().set("margin-top", "5px");
        downloadLink.getStyle().set("display", "inline-block");

        fieldLayout.add(labelComponent, downloadLink);

        return fieldLayout;
    }

    /**
     * Crea un mensaje estilizado indicando que no se ha subido ningún archivo.
     *
     * @param label La etiqueta del campo.
     * @return Un VerticalLayout con la etiqueta y el mensaje de no archivo.
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

        Paragraph noFileMessage = new Paragraph("No se ha subido ningún archivo.");
        noFileMessage.getStyle()
                .set("font-size", "16px")
                .set("font-weight", "normal")
                .set("margin-top", "5px");

        fieldLayout.add(labelComponent, noFileMessage);
        return fieldLayout;
    }

    /**
     * Crea un campo estilizado con etiqueta y valor.
     *
     * @param label La etiqueta del campo.
     * @param value El valor del campo.
     * @return Un VerticalLayout con la etiqueta y el valor.
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
                .set("margin-top", "5px");

        fieldLayout.add(labelComponent, valueComponent);
        return fieldLayout;
    }

    private void openNewProjectDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Nuevo Proyecto");

        FormLayout formLayout = new FormLayout();

        TextField titleField = new TextField("Título");
        TextField shortTitleField = new TextField("Título Corto");
        TextField scopeField = new TextField("Alcance");
        DatePicker startDatePicker = new DatePicker("Fecha de Inicio");

        VerticalLayout memoryUploadContainer = createStyledUpload(
                "Memoria",
                "Subir Memoria",
                new MemoryBuffer(),
                ".pdf", ".docx", ".txt"
        );

        VerticalLayout regulationsUploadContainer = createStyledUpload(
                "Regulaciones del Proyecto",
                "Subir Regulaciones",
                new MemoryBuffer(),
                ".pdf", ".docx", ".txt"
        );

        VerticalLayout specificationsUploadContainer = createStyledUpload(
                "Especificaciones Técnicas",
                "Subir Especificaciones",
                new MemoryBuffer(),
                ".pdf", ".docx", ".txt"
        );

        formLayout.add(
                titleField,
                shortTitleField,
                scopeField,
                startDatePicker,
                memoryUploadContainer,
                regulationsUploadContainer,
                specificationsUploadContainer
        );

        Button saveButton = new Button("Guardar", event -> {
            Project newProject = new Project();
            newProject.setTitle(titleField.getValue());
            newProject.setShortTitle(shortTitleField.getValue());
            newProject.setScope(scopeField.getValue());

            LocalDate localDate = startDatePicker.getValue();
            if (localDate != null) {
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                newProject.setStartDate(date);
            }

            Optional<AppUser> maybeUser = authenticatedUser.get();
            if (maybeUser.isEmpty()) {
                Notification.show("No hay ningún usuario logueado. Por favor, inicia sesión.");
                return;
            }
            AppUser currentUser = maybeUser.get();
            newProject.setApplicantId(currentUser);

            try {
                // Set Memory file
                byte[] memoryContent = extractFileContent(memoryUploadContainer);
                if (memoryContent != null) {
                    newProject.setMemory(memoryContent);
                } else {
                    Notification.show("Por favor, sube un archivo de memoria.");
                    return;
                }

                // Set Project Regulations file
                byte[] regulationsContent = extractFileContent(regulationsUploadContainer);
                if (regulationsContent != null) {
                    newProject.setProjectRegulations(regulationsContent);
                } else {
                    Notification.show("Por favor, sube un archivo de regulaciones del proyecto.");
                    return;
                }

                // Set Technical Specifications file
                byte[] specificationsContent = extractFileContent(specificationsUploadContainer);
                if (specificationsContent != null) {
                    newProject.setTechnicalSpecifications(specificationsContent);
                } else {
                    Notification.show("Por favor, sube un archivo de especificaciones técnicas.");
                    return;
                }

            } catch (Exception e) {
                Notification.show("Error al subir los archivos.");
                e.printStackTrace();
                return;
            }

            projectService.saveProject(newProject);
            projectGrid.setItems(projectService.getAllProjects());
            dialog.close();
        });

        Button cancelButton = new Button("Cancelar", event -> dialog.close());
        HorizontalLayout footerLayout = new HorizontalLayout(saveButton, cancelButton);

        dialog.add(formLayout, footerLayout);
        dialog.open();
    }

    /**
     * Extrae el contenido del archivo desde un contenedor de subida.
     *
     * @param uploadContainer El VerticalLayout que contiene el componente Upload.
     * @return El arreglo de bytes del archivo subido o null si no se subió ningún archivo.
     */
    private byte[] extractFileContent(VerticalLayout uploadContainer) {
        Optional<Upload> uploadOpt = uploadContainer.getChildren()
                .filter(component -> component instanceof Upload)
                .map(component -> (Upload) component)
                .findFirst();

        if (uploadOpt.isPresent()) {
            Upload upload = uploadOpt.get();
            if (upload.getReceiver() instanceof MemoryBuffer memoryBuffer) {
                try {
                    return memoryBuffer.getInputStream().readAllBytes();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Crea un componente de subida estilizado con etiqueta y botón de subida.
     *
     * @param label              La etiqueta para el campo de subida.
     * @param buttonText         El texto para el botón de subida.
     * @param buffer             El MemoryBuffer que recibe el archivo subido.
     * @param acceptedFileTypes  Los tipos de archivos aceptados.
     * @return Un VerticalLayout que contiene el componente de subida estilizado.
     */
    private VerticalLayout createStyledUpload(String label, String buttonText, MemoryBuffer buffer, String... acceptedFileTypes) {
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(acceptedFileTypes);

        // Create the upload button with an icon
        Button uploadButton = new Button(buttonText);
        uploadButton.setIcon(VaadinIcon.UPLOAD.create());
        uploadButton.getStyle()
                .set("margin", "0 auto") // Center the button
                .set("display", "flex")
                .set("align-items", "center");

        upload.setUploadButton(uploadButton);

        // Remove the drop label to simplify the design
        upload.setDropLabel(null);
        upload.setWidthFull();

        // Style adjustments for a clean design
        upload.getElement().getStyle()
                .set("border", "1px dashed #9E9E9E")
                .set("border-radius", "8px")
                .set("padding", "16px")
                .set("text-align", "center")
                .set("margin-bottom", "16px");

        Paragraph uploadLabel = new Paragraph(label);
        uploadLabel.getStyle()
                .set("font-weight", "bold")
                .set("margin-bottom", "8px")
                .set("text-align", "left");

        VerticalLayout uploadContainer = new VerticalLayout(uploadLabel, upload);
        uploadContainer.setWidthFull();
        uploadContainer.setPadding(false);
        uploadContainer.setSpacing(false);

        return uploadContainer;
    }

    // Updated loadPromotersOnStartup method
    private static List<AppUser> cachedPromoters = null;
    private static long cacheTimestamp = 0;
    private static final long CACHE_DURATION_MS = 5 * 24 * 60 * 60 * 1000L; // 5 días

    private void loadPromotersOnStartup() {
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
                List<PromoterApiResponse.PromoterData> promoterDataList = response.getData();

                List<AppUser> existingPromoters = userService.findAllByRole(Role.PROMOTER);

                Map<String, AppUser> existingPromotersMap = existingPromoters.stream()
                        .collect(Collectors.toMap(AppUser::getUsername, Function.identity()));

                List<AppUser> updatedPromoters = new ArrayList<>();

                for (PromoterApiResponse.PromoterData promoterData : promoterDataList) {
                    String nombre = promoterData.getNombre();
                    String cargo = promoterData.getCargo();

                    String email = nombre.toLowerCase().replace(" ", ".") + "@uca.es";

                    AppUser appUser = existingPromotersMap.get(nombre);
                    if (appUser == null) {
                        appUser = new AppUser();
                        appUser.setUsername(nombre);
                        appUser.setPassword("password"); // Plain text password
                        appUser.setEmail(email);
                        appUser.setAcademicPosition(cargo);
                        appUser.setRole(Role.PROMOTER);
                        userService.saveUser(appUser); // Encrypts the password
                    } else {
                        boolean updated = false;
                        if (!appUser.getAcademicPosition().equals(cargo)) {
                            appUser.setAcademicPosition(cargo);
                            updated = true;
                        }
                        if (updated) {
                            userService.saveUser(appUser);
                        }
                    }
                    updatedPromoters.add(appUser);
                }

                Set<String> currentPromoterNames = promoterDataList.stream()
                        .map(PromoterApiResponse.PromoterData::getNombre)
                        .collect(Collectors.toSet());

                for (AppUser existingPromoter : existingPromoters) {
                    if (!currentPromoterNames.contains(existingPromoter.getUsername())) {
                        userService.deleteUser(existingPromoter.getId());
                    }
                }

                cachedPromoters = updatedPromoters;
                cacheTimestamp = System.currentTimeMillis();

                conn.disconnect();
            }

            promoterComboBox.setItems(cachedPromoters);
            promoterComboBox.setItemLabelGenerator(AppUser::getUsername);
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error al cargar los promotores.");
        }
    }
}

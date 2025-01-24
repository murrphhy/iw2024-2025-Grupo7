package grupo7.views.home;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
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
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
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
import grupo7.models.AppUser;
import grupo7.models.Calls;

import grupo7.models.Project;
import grupo7.models.Role;
import grupo7.security.AuthenticatedUser;
import grupo7.services.CallService;
import grupo7.services.ProjectService;
import grupo7.services.PromoterApiResponse;
import grupo7.services.UserService;
import grupo7.services.CallService;
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
@Route("home")
@Menu(order = 1)
@AnonymousAllowed
public class Home extends VerticalLayout {

    private CallService callService;
    private final ProjectService projectService;
    private final UserService userService;

    private final AuthenticatedUser authenticatedUser;
    private final CallService callsService;

    private final Accordion callsAccordion = new Accordion();

    private final ComboBox<Calls> callFilterComboBox = new ComboBox<>("Filtrar Convocatoria");

    private final ComboBox<AppUser> promoterComboBox = new ComboBox<>("Promotor");

    private static List<AppUser> cachedPromoters = null;
    private static long cacheTimestamp = 0;
    private static final long CACHE_DURATION_MS = 5 * 24 * 60 * 60 * 1000L; // 5 días

    @Autowired

    public Home(ProjectService projectService,
                AuthenticatedUser authenticatedUser,
                UserService userService,
                CallService callsService) {
        this.projectService = projectService;
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.callsService = callsService;

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.START);
        getStyle().set("text-align", "left");


        createCallFilterComboBox();

        if (authenticatedUser != null && authenticatedUser.get().isPresent()) {
            Button addProjectButton = new Button("Nuevo Proyecto", e -> openNewProjectDialog());
            HorizontalLayout topBar = new HorizontalLayout(addProjectButton);
            topBar.setWidthFull();
            topBar.setJustifyContentMode(JustifyContentMode.START);
            add(topBar);
        }


        configureCallFilterComboBox();

        callsAccordion.setWidth("90%");
        add(callFilterComboBox, callsAccordion);

        List<Calls> allCalls = callsService.findAll();
        buildAccordion(allCalls);


        loadPromotersOnStartup();
    }


    private void configureCallFilterComboBox() {
        callFilterComboBox.setItemLabelGenerator(Calls::getName);
        callFilterComboBox.setPlaceholder("Seleccione una convocatoria...");

        List<Calls> allCalls = callsService.findAll();
        callFilterComboBox.setItems(allCalls);


        callFilterComboBox.addValueChangeListener(event -> {
            Calls selectedCall = event.getValue();
            if (selectedCall != null) {
                buildAccordion(Collections.singletonList(selectedCall));
            } else {
                buildAccordion(allCalls);
            }
        });
    }

    private void buildAccordion(List<Calls> callsList) {
        callsAccordion.getChildren()
                .collect(Collectors.toList())
                .forEach(comp -> callsAccordion.remove(comp));

        for (Calls call : callsList) {
            Grid<Project> callGrid = createCallGrid(call);
            String panelTitle = "Convocatoria " + call.getName();
            callsAccordion.add(panelTitle, callGrid);
        }
    }

    private Grid<Project> createCallGrid(Calls call) {
        Grid<Project> callGrid = new Grid<>(Project.class, false);
        callGrid.removeAllColumns();

        callGrid.addColumn(project ->
                project.getApplicantId() != null
                        ? project.getApplicantId().getUsername()
                        : "N/A"
        ).setHeader("Solicitante");

        callGrid.addColumn(Project::getPromoterId).setHeader("Promotor");
        callGrid.addColumn(Project::getShortTitle).setHeader("Título corto");
        callGrid.addColumn(Project::getState).setHeader("Estado");
        callGrid.addColumn(proj -> {
            Date d = proj.getStartDate();
            return (d != null) ? d.toString() : "N/A";
        }).setHeader("Fecha inicio").setTextAlign(ColumnTextAlign.END);

        callGrid.setWidth("90%");
        callGrid.setHeight("400px");

        callGrid.setItems(call.getProjects());

        callGrid.addSelectionListener(ev ->
                ev.getFirstSelectedItem().ifPresent(this::openProjectDetailsDialog)
        );

        return callGrid;
    }

    private void openProjectDetailsDialog(Project project) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Detalles del Proyecto");
        dialog.setWidth("60%");

        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.getStyle().set("text-align", "left");

        H2 title = new H2(project.getTitle());
        title.getStyle().set("margin-bottom", "10px");
        detailsLayout.add(title);

        detailsLayout.add(createDetailField("Título corto", project.getShortTitle()));
        detailsLayout.add(createDetailField("Alcance", project.getScope()));

        String promoterDisplay = (project.getPromoterId() != null && !project.getPromoterId().isEmpty())
                ? project.getPromoterId()
                : "Sin Promotor";
        detailsLayout.add(createDetailField("Promotor", promoterDisplay));

        detailsLayout.add(createDetailField("Solicitante",
                (project.getApplicantId() != null) ? project.getApplicantId().getUsername() : "N/A"
        ));

        detailsLayout.add(createDetailField("Estado", project.getState()));
        detailsLayout.add(createDetailField("Fecha inicio",
                (project.getStartDate() != null) ? project.getStartDate().toString() : "N/A"
        ));

        detailsLayout.add(createDownloadField("Memoria", project.getMemory(), project.getShortTitle() + "_memory.pdf"));

        if (project.getProjectRegulations() != null && project.getProjectRegulations().length > 0) {
            detailsLayout.add(createDownloadField(
                    "Regulaciones",
                    project.getProjectRegulations(),
                    project.getShortTitle() + "_project_regulations.pdf"
            ));
        } else {
            detailsLayout.add(createNoFileMessage("Regulaciones"));
        }

        if (project.getTechnicalSpecifications() != null && project.getTechnicalSpecifications().length > 0) {
            detailsLayout.add(createDownloadField(
                    "Especificaciones Técnicas",
                    project.getTechnicalSpecifications(),
                    project.getShortTitle() + "_technical_specifications.pdf"
            ));
        } else {
            detailsLayout.add(createNoFileMessage("Especificaciones Técnicas"));
        }

        Button closeButton = new Button("Cerrar", e -> dialog.close());
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
                .set("margin-top", "5px");

        fieldLayout.add(labelComponent, valueComponent);
        return fieldLayout;
    }

    private VerticalLayout createDownloadField(String label, byte[] fileContent, String fileName) {
        VerticalLayout fieldLayout = new VerticalLayout();
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);

        Paragraph labelComp = new Paragraph(label);
        labelComp.getStyle()
                .set("color", "darkgray")
                .set("font-weight", "bold")
                .set("margin-bottom", "0");

        if (fileContent == null || fileContent.length == 0) {
            fieldLayout.add(labelComp, new Paragraph("Sin archivo"));
            return fieldLayout;
        }

        StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(fileContent));
        resource.setContentType("application/pdf");

        Anchor downloadLink = new Anchor(resource, "Descargar " + label);
        downloadLink.getElement().setAttribute("download", true);
        downloadLink.getStyle().set("margin-top", "5px")
                .set("display", "inline-block");

        fieldLayout.add(labelComp, downloadLink);
        return fieldLayout;
    }

    private VerticalLayout createNoFileMessage(String label) {
        VerticalLayout fieldLayout = new VerticalLayout();
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);

        Paragraph labelComp = new Paragraph(label);
        labelComp.getStyle()
                .set("color", "darkgray")
                .set("font-weight", "bold")
                .set("margin-bottom", "0");

        Paragraph noFileMsg = new Paragraph("No se ha subido ningún archivo");
        noFileMsg.getStyle().set("margin-top", "5px");

        fieldLayout.add(labelComp, noFileMsg);
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
        ComboBox<AppUser> promoterCB = new ComboBox<>("Promotor");
        promoterCB.setItems(userService.findAllByRole(Role.PROMOTER));
        promoterCB.setItemLabelGenerator(u ->
                u.getUsername() + " (" + u.getAcademicPosition() + ")"
        );

        VerticalLayout memoryUploadContainer = createStyledUpload(
                "Memoria", "Subir Memoria", new MemoryBuffer(), ".pdf", ".docx", ".txt"
        );
        VerticalLayout regulationsUploadContainer = createStyledUpload(
                "Regulaciones", "Subir Regulaciones", new MemoryBuffer(), ".pdf", ".docx", ".txt"
        );
        VerticalLayout specificationsUploadContainer = createStyledUpload(
                "Especificaciones Técnicas", "Subir Especificaciones", new MemoryBuffer(), ".pdf", ".docx", ".txt"
        );

        formLayout.add(
                titleField, shortTitleField, scopeField, startDatePicker,
                promoterCB, memoryUploadContainer, regulationsUploadContainer,
                specificationsUploadContainer
        );

        Button saveButton = new Button("Guardar", e -> {
            Project newProject = new Project();
            if (titleField.isEmpty()) {
                Notification.show("El campo 'Título' es obligatorio.");
                return;
            }
            newProject.setTitle(titleField.getValue());

            if (shortTitleField.isEmpty()) {
                Notification.show("El campo 'Título Corto' es obligatorio.");
                return;
            }
            newProject.setShortTitle(shortTitleField.getValue());

            if (scopeField.isEmpty()) {
                Notification.show("El campo 'Alcance' es obligatorio.");
                return;
            }
            newProject.setScope(scopeField.getValue());

            LocalDate localDate = startDatePicker.getValue();
            if (localDate == null) {
                Notification.show("El campo 'Fecha de Inicio' es obligatorio.");
                return;
            } else {
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                newProject.setStartDate(date);
            }

            Optional<AppUser> maybeUser = authenticatedUser.get();
            if (maybeUser.isEmpty()) {
                Notification.show("No hay un usuario autenticado.");
                return;
            }
            newProject.setApplicantId(maybeUser.get());

            AppUser promoter = promoterCB.getValue();
            if (promoter == null) {
                Notification.show("Debe seleccionar un promotor.");
                return;
            }
            newProject.setPromoterId(promoter.getUsername());

            // Subir archivos
            try {
                byte[] memoryContent = extractFileContent(memoryUploadContainer);
                if (memoryContent != null) {
                    newProject.setMemory(memoryContent);
                } else {
                    Notification.show("Debe subir un archivo de Memoria.");
                    return;
                }

                byte[] regsContent = extractFileContent(regulationsUploadContainer);
                if (regsContent != null) {
                    newProject.setProjectRegulations(regsContent);
                } else {
                    Notification.show("Debe subir Regulaciones.");
                    return;
                }

                byte[] specsContent = extractFileContent(specificationsUploadContainer);
                if (specsContent != null) {
                    newProject.setTechnicalSpecifications(specsContent);
                } else {
                    Notification.show("Debe subir Especificaciones Técnicas.");
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Notification.show("Error al subir archivos.");
                return;
            }

            projectService.saveProject(newProject);

            Notification.show("Proyecto creado exitosamente",
                            3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            dialog.close();
        });

        Button cancelButton = new Button("Cancelar", e -> dialog.close());

        HorizontalLayout footerLayout = new HorizontalLayout(saveButton, cancelButton);
        dialog.add(formLayout, footerLayout);
        dialog.open();
    }

    private VerticalLayout createStyledUpload(String label, String buttonText, MemoryBuffer buffer, String... acceptedFileTypes) {
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(acceptedFileTypes);

        Button uploadButton = new Button(buttonText, VaadinIcon.UPLOAD.create());
        uploadButton.getStyle()
                .set("margin", "0 auto")
                .set("display", "flex")
                .set("align-items", "center");

        upload.setUploadButton(uploadButton);
        upload.setDropLabel(null);
        upload.setWidthFull();

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

    private byte[] extractFileContent(VerticalLayout uploadContainer) {
        return uploadContainer.getChildren()
                .filter(c -> c instanceof Upload)
                .map(c -> (Upload) c)
                .findFirst()
                .map(u -> {
                    if (u.getReceiver() instanceof MemoryBuffer memBuf) {
                        try {
                            return memBuf.getInputStream().readAllBytes();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                    return null;
                })
                .orElse(null);
    }

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
                        appUser.setPassword("password");
                        appUser.setEmail(email);
                        appUser.setAcademicPosition(cargo);
                        appUser.setRole(Role.PROMOTER);
                        userService.saveUser(appUser);
                    } else {
                        boolean updated = false;
                        if (!Objects.equals(appUser.getAcademicPosition(), cargo)) {
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
            Notification.show("Error al cargar promotores");
        }
    }
}
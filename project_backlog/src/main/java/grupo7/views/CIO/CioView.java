package grupo7.views.CIO;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import grupo7.models.Project;
import grupo7.services.ProjectService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageTitle("Cio View")
@Route("cio-dashboard")
@Menu(order = 3)
@PermitAll
public class CioView extends VerticalLayout {

    private final ProjectService projectService;
    private final Grid<Project> projectGrid = new Grid<>(Project.class);
    private final Binder<Project> binder = new Binder<>(Project.class);

    // Campos para priorización
    private final NumberField strategicAlignmentField = new NumberField("Alineamiento Estratégico");
    private final NumberField technicalSuitabilityField = new NumberField("Idoneidad Técnica");
    private final NumberField resourcesField = new NumberField("Recursos Disponibles");
    private final Button saveButton = new Button("Guardar Prioridad");

    @Autowired
    public CioView(ProjectService projectService) {
        this.projectService = projectService;

        binder.forField(strategicAlignmentField)
          .bind(Project::getStrategicAlignment, Project::setStrategicAlignment);

        binder.forField(technicalSuitabilityField)
          .bind(Project::getTechnicalSuitability, Project::setTechnicalSuitability);

        binder.forField(resourcesField)
          .bind(Project::getAvailableResources, Project::setAvailableResources);

        // Configurar layout
        setSizeFull();
        add(createProjectGrid(), createPrioritizationForm());
        refreshGrid();
    }

    private Grid<Project> createProjectGrid() {
        projectGrid.setColumns("id", "title", "state", "startDate");
        projectGrid.asSingleSelect().addValueChangeListener(event -> editProject(event.getValue()));
        return projectGrid;
    }

    private VerticalLayout createPrioritizationForm() {
        strategicAlignmentField.setMin(0);
        strategicAlignmentField.setMax(10);

        technicalSuitabilityField.setMin(0);
        technicalSuitabilityField.setMax(10);

        resourcesField.setMin(0);
        resourcesField.setMax(10);

        saveButton.addClickListener(event -> savePrioritization());

        HorizontalLayout formLayout = new HorizontalLayout(
                strategicAlignmentField,
                technicalSuitabilityField,
                resourcesField,
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

    private void savePrioritization() {
        Project project = binder.getBean();
        if (project != null) {
            projectService.saveProject(project);
            System.out.println("Priorización guardada: " +
                    "Alineamiento Estratégico: " + strategicAlignmentField.getValue() +
                    ", Idoneidad Técnica: " + technicalSuitabilityField.getValue() +
                    ", Recursos: " + resourcesField.getValue());
            refreshGrid();
            clearForm();
        }
    }

    private void clearForm() {
        binder.setBean(null);
        strategicAlignmentField.clear();
        technicalSuitabilityField.clear();
        resourcesField.clear();
    }

    private void refreshGrid() {
        List<Project> projects = projectService.getAllProjects();
        projectGrid.setItems(projects);
    }
}

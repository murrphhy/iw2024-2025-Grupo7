package grupo7.views;

import grupo7.models.Project;
import grupo7.services.ProjectService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("technical-area")
public class TechnicalAreaView extends VerticalLayout {

    private final ProjectService projectService;
    private final Grid<Project> projectGrid = new Grid<>(Project.class);

    @Autowired
    public TechnicalAreaView(ProjectService projectService) {
        this.projectService = projectService;

        addClassName("technical-area-view");
        setSizeFull();

        configureGrid();
        add(projectGrid);

        updateProjectList();
    }

    private void configureGrid() {
        projectGrid.addClassName("project-grid");
        projectGrid.setSizeFull();
        projectGrid.setColumns("id", "title", "shortTitle", "state", "startDate");
        projectGrid.addComponentColumn(project -> createDetailsButton(project))
                .setHeader("Actions");
    }

    private Button createDetailsButton(Project project) {
        return new Button("Details", click -> {
            // Implementar lógica de evaluación técnica
        });
    }

    private void updateProjectList() {
        projectGrid.setItems(projectService.findAll());
    }
}

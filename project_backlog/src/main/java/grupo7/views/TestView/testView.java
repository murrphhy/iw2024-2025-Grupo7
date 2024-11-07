package grupo7.views.TestView;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

public class testView  extends VerticalLayout {
    public testView() {
        setSizeFull();
        setSpacing(true);
        setMargin(true);
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        H2 titlePage = new H2("Test");
        titlePage.setWidth("100%");
    }
}

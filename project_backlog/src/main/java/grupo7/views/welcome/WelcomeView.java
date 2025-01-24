package grupo7.views.welcome;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("")
@PageTitle("Bienvenido | Cartera de proyectos UCA")
@AnonymousAllowed
public class WelcomeView extends VerticalLayout {

    public WelcomeView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);
        getStyle().set("margin-top", "6rem");

        Image logo = new Image("https://gabcomunicacion.uca.es/wp-content/uploads/2017/05/Logo-V2-Color-Imprsi%C3%B3n-100x133-mm-jpg.jpg?u", "Logo UCA");
        logo.setWidth("300px");
        logo.getStyle().set("margin-bottom", "2rem");

        H1 title = new H1("Cartera de proyectos de la Universidad de Cádiz");

        Button viewProjectsButton = new Button("Ver proyectos");
        viewProjectsButton.getStyle().set("margin-top", "5rem");
        viewProjectsButton.addClickListener(e -> UI.getCurrent().navigate("home"));

        add(logo, title, viewProjectsButton);
    }
}

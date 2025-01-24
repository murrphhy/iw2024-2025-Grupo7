package grupo7.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import grupo7.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

import com.vaadin.flow.i18n.I18NProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The main view is a top-level placeholder for other views.
 */
@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {

    private H1 viewTitle;
    private final Button logoutButton;

    public MainLayout(@Autowired AuthenticatedUser authenticationContext, I18NProvider i18nProvider) {

        if (authenticationContext.get().isPresent()) {
            logoutButton = new Button("Cerrar Sesión", click -> authenticationContext.logout());
        } else {
            logoutButton = new Button("Iniciar Sesión", click -> UI.getCurrent().navigate("login"));
        }

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent(i18nProvider);
    }


    private void addHeaderContent(I18NProvider i18nProvider) {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        ComboBox<Locale> languageSelector = createLanguageSelector(i18nProvider);

        // Añadir elementos al encabezado
        HorizontalLayout headerLayout = new HorizontalLayout(toggle, viewTitle, languageSelector);
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        addToNavbar(headerLayout);
    }

    private ComboBox<Locale> createLanguageSelector(I18NProvider i18nProvider) {
        ComboBox<Locale> languageSelector = new ComboBox<>();
        languageSelector.setWidth("150px");

        Map<Locale, String> languageLabels = new HashMap<>();
        languageLabels.put(Locale.forLanguageTag("es"), "Español");
        languageLabels.put(Locale.forLanguageTag("en"), "English");
        languageLabels.put(Locale.forLanguageTag("fr"), "Français");

        languageSelector.setItems(i18nProvider.getProvidedLocales());
        languageSelector.setItemLabelGenerator(locale -> languageLabels.getOrDefault(locale, locale.getDisplayLanguage()));

        languageSelector.addValueChangeListener(event -> {
            if (event.getValue() != null && !event.getValue().equals(UI.getCurrent().getSession().getLocale())) {
                UI.getCurrent().getSession().setLocale(event.getValue());
                UI.getCurrent().getPage().reload(); // Recargar la página para aplicar el idioma
            }
        });

        // Establecer el idioma actual como seleccionado
        languageSelector.setValue(UI.getCurrent().getSession().getLocale());

        return languageSelector;
    }

    private void addDrawerContent() {
        Span appName = new Span("project_backlog");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        menuEntries.forEach(entry -> {
            if (entry.icon() != null) {
                nav.addItem(new SideNavItem(entry.title(), entry.path(), new SvgIcon(entry.icon())));
            } else {
                nav.addItem(new SideNavItem(entry.title(), entry.path()));
            }
        });

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.add(logoutButton);

        // Botón de registro si no está autenticado
        if (!logoutButton.getText().equals("Cerrar Sesión")) {
            Button registerButton = new Button("Registro", click -> UI.getCurrent().navigate("register"));
            layout.add(registerButton);
        }

        layout.getStyle().set("margin-top", "auto");
        layout.getStyle().set("margin-left", "auto");

        return layout;
    }


    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent()).orElse("");
    }
}

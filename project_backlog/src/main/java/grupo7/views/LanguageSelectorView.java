package grupo7.views;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

@Route("language-selector")
public class LanguageSelectorView extends VerticalLayout {

    @Autowired
    public LanguageSelectorView(I18NProvider i18nProvider) {
        ComboBox<Locale> languageSelector = new ComboBox<>("Language");
        languageSelector.setItems(i18nProvider.getProvidedLocales());
        languageSelector.setItemLabelGenerator(Locale::getDisplayLanguage);

        languageSelector.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                getUI().ifPresent(ui -> {
                    ui.getSession().setLocale(event.getValue());
                    ui.getPage().reload(); // Recargar la página para aplicar el nuevo idioma
                });
            }
        });

        add(new HorizontalLayout(languageSelector));
    }
}

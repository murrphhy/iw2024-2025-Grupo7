package grupo7.config;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CustomI18NProvider implements I18NProvider {

    private static final List<Locale> SUPPORTED_LOCALES = Arrays.asList(
            Locale.forLanguageTag("es"), // Español
            Locale.forLanguageTag("en"), // Inglés
            Locale.forLanguageTag("fr")  // Francés
    );

    private final ResourceBundle.Control control = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES);

    @Override
    public List<Locale> getProvidedLocales() {
        return SUPPORTED_LOCALES;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null || locale == null) {
            return "";
        }
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale, control);
            String value = bundle.getString(key);

            // Reemplaza placeholders como {0}, {1}, etc., con los parámetros proporcionados
            if (params != null && params.length > 0) {
                value = String.format(value, params);
            }
            return value;
        } catch (MissingResourceException e) {
            return key; // Muestra la clave como fallback si no se encuentra la traducción
        }
    }
}
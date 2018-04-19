package dps.webapplication.i18n;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@ApplicationScoped
public class Locales {

    Map<String,Locale> locales = new HashMap<>();

    Locale getLocale(String languageTag)
    {
        Locale locale = locales.get(languageTag);
        if (locale == null) {
            locale = Locale.forLanguageTag(languageTag);
            locales.put(languageTag,locale);
        }
        return locale;
    }

}

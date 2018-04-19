package dps.webapplication.i18n;

import javax.inject.Inject;
import java.util.Locale;
import java.util.ResourceBundle;

abstract public class AbstractLocale {

    @Inject
    Locales locales;

    protected Locale locale;

    abstract AbstractLocale getParentLocale();

    public Locale getLocale() {
        if (locale == null) {
            AbstractLocale parentLocale = getParentLocale();
            if (parentLocale == null) throw new NullPointerException();
            return parentLocale.getLocale();
        }
        return locale;
    }
    public void setLocale(String languageTag) {
        this.setLocale(locales.getLocale(languageTag));
    }
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

}

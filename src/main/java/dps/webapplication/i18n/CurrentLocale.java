package dps.webapplication.i18n;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named("CurrentLocale")
@RequestScoped
public class CurrentLocale extends AbstractLocale {

    @Inject
    DefaultLocale defaultLocale;

    @Inject
    SessionLocale sessionLocale;

    @Override
    AbstractLocale getParentLocale() {
        return sessionLocale;
    }

    public boolean isDefault() {
        return defaultLocale.getLocale() == this.getLocale();
    }

    @Override
    public void setLocale(String languageTag) {
        super.setLocale(languageTag);
    }
}
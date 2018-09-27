package dps.webapplication.i18n;

import dps.commons.startup.Startup;
import dps.webapplication.configuration.Settings;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@Startup
public class DefaultLocale extends AbstractLocale {

    @Inject
    Settings applicationSettings;

    @Override
    AbstractLocale getParentLocale() {
        return null;
    }

    @PostConstruct
    void init() {
        this.setLocale(applicationSettings.getLocale());
    }

}

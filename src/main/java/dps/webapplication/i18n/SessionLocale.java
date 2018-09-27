package dps.webapplication.i18n;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class SessionLocale  extends AbstractLocale implements Serializable {

    @Inject
    DefaultLocale defaultLocale;

    @Override
    AbstractLocale getParentLocale() {
        return defaultLocale;
    }

}

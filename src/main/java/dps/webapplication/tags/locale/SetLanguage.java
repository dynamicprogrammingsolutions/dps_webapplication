package dps.webapplication.tags.locale;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.Date;
import dps.webapplication.i18n.SessionLocale;

public class SetLanguage extends SimpleTagSupport {

    String lan = null;

    public void setLan(String lan)
    {
        this.lan = lan;
    }

    @Override
    public void doTag() throws JspException, IOException {
        if (lan != null && !lan.isEmpty()) {
            SessionLocale sessionLocale = CDI.current().select(SessionLocale.class).get();
            sessionLocale.setLocale(lan);
        }
    }
}

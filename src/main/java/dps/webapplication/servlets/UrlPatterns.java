package dps.webapplication.servlets;

import javax.enterprise.context.ApplicationScoped;
import java.util.regex.Pattern;

@ApplicationScoped
public class UrlPatterns {

    volatile Pattern indexPattern = Pattern.compile("^(?:/|/index|/index\\.html)?$");
    volatile Pattern jspPattern = Pattern.compile("^/(index|(?:pages/[a-zA-Z0-9-_/]+))$");
    volatile Pattern resourcePattern = Pattern.compile("^/(?:scripts|images|styles|views|[a-zA-Z0-9-_]+\\.html)(?:/.*)?$");
    volatile Pattern sitePattern = Pattern.compile("^/(?:[a-zA-Z]*)(?:/.*)?$");

    public Pattern getIndexPattern() {
        return indexPattern;
    }

    public Pattern getJspPattern() {
        return jspPattern;
    }

    public Pattern getResourcePattern() {
        return resourcePattern;
    }

    public Pattern getSitePattern() {
        return sitePattern;
    }
}

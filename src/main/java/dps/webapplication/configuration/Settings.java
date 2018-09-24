package dps.webapplication.configuration;

import java.util.HashMap;

public interface Settings {

    String getHost();
    String getRoot();
    String getLocale();
    HashMap<String, String> getLoglevels();

}

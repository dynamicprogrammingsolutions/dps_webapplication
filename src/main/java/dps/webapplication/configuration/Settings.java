package dps.webapplication.configuration;

import dps.commons.configuration.XmlConfiguration;
import dps.commons.startup.Startup;
import dps.logging.HasLogger;
import dps.logging.Loggers;
import org.apache.commons.beanutils.BeanUtils;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*@Named("Settings")
@ApplicationScoped
@Startup*/
@XmlRootElement
public class Settings/* extends XmlConfiguration implements HasLogger*/ {

    /*@Resource(name="settingsfile")
    String settingsFile;
    protected String getSettingsFile() {
        return settingsFile;
    }

    @PostConstruct
    void init() {
        if (loglevels != null) {
            Loggers loggers = Loggers.getInstance();
            for (Map.Entry<String, String> entry : loglevels.entrySet()) {
                if (entry.getKey() == null) {
                    throw new RuntimeException("invalid loglevel entry: " + entry);
                }
                Logger logger = loggers.getLogger(entry.getKey());
                logger.setLevel(Level.parse(entry.getValue()));
                logInfo(logger.getName() + " loglevel: " + logger.getLevel());
            }
        } else {
            logInfo("No loglevel entry in settings file");
        }
    }*/

    HashMap<String,String> loglevels;
    String host;
    String root;
    String locale;
    //String name;


    public String getHost() {
        return host;
    }

    @XmlElement
    public void setHost(String host) {
        this.host = host;
    }


    public String getRoot() {
        return root;
    }

    @XmlElement
    public void setRoot(String root) {
        this.root = root;
    }

    public String getLocale() {
        return locale;
    }

    @XmlElement
    public void setLocale(String locale) {
        this.locale = locale;
    }

    public HashMap<String, String> getLoglevels() {
        return loglevels;
    }

    @XmlElement
    public void setLoglevels(HashMap<String, String> loglevels) {
        this.loglevels = loglevels;
    }

    /*
    public String getName() {
        return name;
    }

    @XmlElement
    public void setName(String name) {
        this.name = name;
    }
    */

}

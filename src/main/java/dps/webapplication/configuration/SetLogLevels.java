package dps.webapplication.configuration;

import dps.commons.startup.Startup;
import dps.logging.HasLogger;
import dps.logging.Loggers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.HttpCookie;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Startup
public class SetLogLevels implements HasLogger {

    @Inject
    Settings settings;

    @PostConstruct
    void init() {
        if (settings.loglevels != null) {
            Loggers loggers = Loggers.getInstance();
            for (Map.Entry<String, String> entry : settings.loglevels.entrySet()) {
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
    }

}

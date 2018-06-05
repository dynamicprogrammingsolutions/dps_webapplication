package dps.webapplication.configuration;

import dps.commons.reflect.ReflectHelper;
import dps.commons.startup.Startup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@Startup
public class Configuration {

    private String homeDir;
    private String userDir;

    Map<String,Object> config = new HashMap<>();

    @Inject Settings settings;

    @PostConstruct
    void init() {
        userDir = System.getProperty("user.dir");
        homeDir = System.getProperty("user.home");
    }

    public void registerConfig(String name, Class<?> configClass) {
        if (config.get(name) == null) {

            Object loadedConfig = this.loadConfig(name,configClass);
            if (loadedConfig != null) {
                config.put(name,loadedConfig);
            } else {
                try {
                    Object newConfig = configClass.getConstructor().newInstance();
                    this.saveConfig(name,newConfig);
                    config.put(name,newConfig);

                } catch (NoSuchMethodException e) {
                    throw new ConfigurationException("Class " + configClass.getSimpleName() + " has no no-arg constructor");
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    throw new ConfigurationException("Unkonw error",e);
                }
            }

        } else {
            if (!configClass.equals(config.get(name).getClass())) {
                throw new ConfigurationException("Configuration with same name exists");
            }
        }
    }

    public Object get(String name)
    {
        return config.get(name);
    }

    public <T> T get(String name, Class<T> configClass)
    {
        Object configObject = config.get(name);
        if (configClass.isInstance(configObject)) {
            return (T) configObject;
        } else {
            return null;
        }
    }

    public void save(String name)
    {
        Object o = get(name);
        this.saveConfig(name,o);
    }

    private Object loadConfig(String name, Class<?> configClass)
    {
        try {
            Path configDirPath = getConfigDir();
            Path configFilePath = configDirPath.resolve("./" + name);
            if (Files.exists(configFilePath)) {
                InputStream inputStream = Files.newInputStream(configFilePath);
                JAXBContext jaxbContext = JAXBContext.newInstance(configClass);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                Object configObject = unmarshaller.unmarshal(inputStream);
                return configObject;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new ConfigurationException("IOException",e);
        } catch (JAXBException e) {
            throw new ConfigurationException("JAXB Exception",e);
        }
    }

    private void saveConfig(String name, Object configObject)
    {
        try {
            Path configFilePath = getConfigDir().resolve("./" + name);
            OutputStream outputStream = Files.newOutputStream(configFilePath);
            JAXBContext jaxbContext = JAXBContext.newInstance(configObject.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(configObject,outputStream);
        } catch (IOException e) {
            throw new ConfigurationException("IOException",e);
        } catch (JAXBException e) {
            throw new ConfigurationException("JAXB Exception",e);
        }
    }

    private Path getConfigDir() throws IOException {
        Path homePath = Paths.get(homeDir);
        if (!Files.exists(homePath)) {
            throw new ConfigurationException("home directory doesn't exist");
        }
        Path configDirPath = homePath.resolve("./."+settings.getName());
        if (!Files.exists(configDirPath)) {
            Files.createDirectory(configDirPath);
        }
        return configDirPath;
    }

}

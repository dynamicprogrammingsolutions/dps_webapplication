package dps.webapplication.configuration;

import dps.commons.reflect.ReflectHelper;
import dps.commons.startup.Startup;
import dps.logging.HasLogger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
@Startup
public class Configuration implements HasLogger {

    private String homeDir;
    private String userDir;

    Map<String,Object> config = new HashMap<>();
    Map<Class<?>,Object> configByClass = new HashMap<>();
    Map<Class<?>,String> nameByClass = new HashMap<>();

    @Resource(name="applicationName")
    String applicationName;

    //@Inject Settings settings;

    @PostConstruct
    void init() {
        userDir = System.getProperty("user.dir");
        homeDir = System.getProperty("user.home");
        logInfo("application name: "+applicationName);
    }

    public <T> T reloadConfig(Class<T> configClass)
    {
        T loadedConfig = this.loadConfig(nameByClass.get(configClass),configClass);
        if (loadedConfig == null) {
            throw new ConfigurationException("Coudn't reload config from disk");
        }
        return loadedConfig;
    }

    public void registerConfig(String name, Class<?> configClass) {
        if (config.get(name) == null) {

            Object loadedConfig = this.loadConfig(name,configClass);
            if (loadedConfig != null) {
                put(name,loadedConfig);
            } else {

                Object defaultConfig = loadDefaultConfig(name,configClass);
                if (defaultConfig != null) {
                    saveConfig(name,defaultConfig);
                    put(name,defaultConfig);
                } else {
                    try {
                            Object newConfig = configClass.getConstructor().newInstance();
                            this.saveConfig(name,newConfig);
                            put(name,newConfig);
                    } catch (NoSuchMethodException e) {
                        throw new ConfigurationException("Class " + configClass.getSimpleName() + " has no no-arg constructor");
                    } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        throw new ConfigurationException("Unkonw error",e);
                    }
                }

            }

        } else {
            if (!configClass.equals(config.get(name).getClass())) {
                throw new ConfigurationException("Configuration with same name exists");
            }
        }
    }

    private void put(String name, Object obj)
    {
        config.put(name,obj);
        configByClass.put(obj.getClass(),obj);
        nameByClass.put(obj.getClass(),name);
    }

    public <T> T get(Class<T> clazz) {
        return (T)configByClass.get(clazz);
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

    public void save(Object configObject)
    {
        String name = nameByClass.get(configObject.getClass());
        if (name != null) {
            logInfo("saving object "+configObject);
            this.saveConfig(name, configObject);
            put(name, configObject);
        } else {
            throw new ConfigurationException("configuration class "+configObject.getClass().getSimpleName()+" not registered");
        }
    }


    private <T> T loadDefaultConfig(String name, Class<T> configClass)
    {
        try {
            try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("META-INF/" + name + ".xml");) {

                if (inputStream == null) return null;
                JAXBContext jaxbContext = JAXBContext.newInstance(configClass);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                T configObject = (T)unmarshaller.unmarshal(inputStream);
                return configObject;

            }
        } catch (IOException e) {
            throw new ConfigurationException("IOException",e);
        } catch (JAXBException e) {
            throw new ConfigurationException("JAXB Exception",e);
        }
    }

    private <T> T loadConfig(String name, Class<T> configClass)
    {
        try {
            Path configFilePath = getConfigFile(name);
            if (Files.exists(configFilePath)) {

                try (InputStream inputStream = Files.newInputStream(configFilePath)) {
                    JAXBContext jaxbContext = JAXBContext.newInstance(configClass);
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    T configObject = (T)unmarshaller.unmarshal(inputStream);
                    return configObject;
                }

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
            Path configFile = getConfigFile(name);
            try (OutputStream outputStream = Files.newOutputStream(configFile)) {
                logInfo("saving object to "+configFile);
                JAXBContext jaxbContext = JAXBContext.newInstance(configObject.getClass());
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
                marshaller.marshal(configObject, outputStream);
            }
            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);
            Files.setPosixFilePermissions(configFile,perms);
        } catch (IOException e) {
            throw new ConfigurationException("IOException",e);
        } catch (JAXBException e) {
            throw new ConfigurationException("JAXB Exception",e);
        }
    }

    private Path getConfigFile(String name) throws IOException
    {
        return getConfigDir().resolve("./" + name + ".xml");
    }

    private Path getConfigDir() throws IOException {
        Path homePath = Paths.get(homeDir);
        if (!Files.exists(homePath)) {
            throw new ConfigurationException("home directory doesn't exist");
        }
        Path configDirPath = homePath.resolve("./."+applicationName);
        if (!Files.exists(configDirPath)) {
            Files.createDirectory(configDirPath);
        }
        return configDirPath;
    }

}

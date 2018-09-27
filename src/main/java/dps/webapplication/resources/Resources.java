package dps.webapplication.resources;

import dps.logging.HasLogger;
import dps.webapplication.i18n.CurrentLocale;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@ApplicationScoped
public class Resources implements HasLogger {

    @Inject
    CurrentLocale currentLocale;

    int bufferSize = 512;

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void writeTo(Reader reader, Writer writer) {
        try {
            int num;
            char[] ch = new char[bufferSize];
            while ((num = reader.read(ch)) != -1) {
                writer.write(ch, 0, num);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCacheName(String name) {
        Path path = Paths.get(name);
        Path localizedPath = path.getParent().resolve(Paths.get(currentLocale.getLocale().toLanguageTag())).resolve(path.getFileName());
        return localizedPath.toString();
    }

    public Date getUpdated(Path path) {
        try {
            return new Date(Files.getLastModifiedTime(path).toMillis());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Path getResourcePath(String name)
    {
        URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource(name);
        if (resourceUrl == null) return null;
        Path path = Paths.get(resourceUrl.getFile());
        if (!Files.exists(path)) {
            return null;
        }
        Path localizedPath = path.getParent().resolve(Paths.get(currentLocale.getLocale().toLanguageTag())).resolve(path.getFileName());
        if (Files.exists(localizedPath)) path = localizedPath;
        return path;
    }

    public Reader getResource(Path path)
    {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(path)));
            return reader;
        } catch (IOException e) {
            logSevere("Couldn't load resource",e);
            return null;
        }
    }

    public Reader getResource(String name)
    {
        Path path = getResourcePath(name);
        if (path == null) return null;
        return getResource(path);
    }

}

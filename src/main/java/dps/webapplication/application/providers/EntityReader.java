package dps.webapplication.application.providers;

import dps.webapplication.crudmodel.CRUDModel;
import dps.webapplication.crudmodel.CRUDModelException;
import dps.webapplication.crudmodel.CRUDModels;
import dps.webapplication.crudmodel.ConversionException;
import dps.webapplication.crudmodel.handlers.EntityWriterHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Map;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class EntityReader implements MessageBodyReader<Object> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (CRUDModels.getInstance().checkModel(type)) {
            try {
                CRUDModel model = CRUDModels.getInstance().getModel(type);
                if (model.checkModel(EntityWriterHandler.class)) return true;
            } catch (CRUDModelException e) {
                throw new WebApplicationException(e);
            }
        }
        return false;
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {

        CRUDModel model;
        try {
            model = CRUDModels.getInstance().getModel(type);
        } catch (CRUDModelException e) {
            throw new WebApplicationException(e);
        }

        Map<String, EntityWriterHandler> handlers = model.getModel(EntityWriterHandler.class);

        JsonReader reader = Json.createReader(entityStream);
        JsonObject jsonObject = reader.readObject();

        Object o = null;
        try {
            o = model.createNewObject();
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new WebApplicationException(e);
        }

        for (Map.Entry<String,EntityWriterHandler> entry: handlers.entrySet()) {
            try {
                entry.getValue().handle(o,jsonObject);
            } catch (ConversionException | InvocationTargetException | IllegalAccessException e) {
                throw new WebApplicationException(e);
            }
        }

        reader.close();

        return o;
    }
}

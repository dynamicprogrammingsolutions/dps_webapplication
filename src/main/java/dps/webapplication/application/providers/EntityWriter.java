package dps.webapplication.application.providers;

import dps.webapplication.crudmodel.CRUDModel;
import dps.webapplication.crudmodel.CRUDModelException;
import dps.webapplication.crudmodel.CRUDModels;
import dps.webapplication.crudmodel.ConversionException;
import dps.webapplication.crudmodel.handlers.EntityWriterHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Map;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class EntityWriter implements MessageBodyWriter<Object> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
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
    public long getSize(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        CRUDModel model;
        try {
            model = CRUDModels.getInstance().getModel(type);
        } catch (CRUDModelException e) {
            throw new WebApplicationException(e);
        }

        Map<String, EntityWriterHandler> handlers = model.getModel(EntityWriterHandler.class);
        for (Map.Entry<String,EntityWriterHandler> entry: handlers.entrySet()) {
            try {
                entry.getValue().handle(o,objectBuilder);
            } catch (InvocationTargetException | IllegalAccessException | ConversionException e) {
                throw new WebApplicationException(e);
            }
        }

        JsonObject json = objectBuilder.build();
        JsonWriter writer = Json.createWriter(entityStream);
        writer.writeObject(json);
        writer.close();

    }
}

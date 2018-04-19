package dps.webapplication.application.providers;

import dps.webapplication.crudmodel.CRUDModel;
import dps.webapplication.crudmodel.CRUDModelException;
import dps.webapplication.crudmodel.CRUDModels;
import dps.webapplication.crudmodel.ConversionException;
import dps.webapplication.crudmodel.handlers.EntityListWriterHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class EntityListWriter implements MessageBodyWriter<List<?>> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)genericType;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length == 0) return false;
            Type actualTypeArgument = actualTypeArguments[0];
            if (actualTypeArgument instanceof Class<?>) {
                Class<?>entityClass = (Class<?>)actualTypeArgument;
                if (CRUDModels.getInstance().checkModel(entityClass)) {
                    try {
                        CRUDModel model = CRUDModels.getInstance().getModel(entityClass);
                        if (model.checkModel(EntityListWriterHandler.class)) return true;
                    } catch (CRUDModelException e) {
                        throw new WebApplicationException(e);
                    }
                }
            }
        }

        return false;

    }

    @Override
    public long getSize(List<?> o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(List<?> o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

        CRUDModel model;
        Class<?>entityClass = (Class<?>)((ParameterizedType)genericType).getActualTypeArguments()[0];
        try {
            model = CRUDModels.getInstance().getModel(entityClass);
        } catch (CRUDModelException e) {
            throw new WebApplicationException(e);
        }

        JsonGenerator generator = Json.createGenerator(entityStream);
        generator.writeStartArray();

        for (Object item: o) {

            generator.writeStartObject();

            Map<String, EntityListWriterHandler> handlers = model.getModel(EntityListWriterHandler.class);
            for (Map.Entry<String, EntityListWriterHandler> entry : handlers.entrySet()) {
                try {
                    entry.getValue().handle(item, generator);
                } catch (InvocationTargetException | IllegalAccessException | ConversionException e) {
                    throw new WebApplicationException(e);
                }
            }

            generator.writeEnd();

        }
        generator.writeEnd();

        generator.close();

    }
}

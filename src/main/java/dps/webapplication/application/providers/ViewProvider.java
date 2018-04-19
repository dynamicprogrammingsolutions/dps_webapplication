package dps.webapplication.application.providers;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(MediaType.TEXT_HTML)
public class ViewProvider implements MessageBodyWriter<View> {

    @Context
    Providers providers;

    @Inject
    ForwardWriter forwardWriter;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (type.isAssignableFrom(View.class)) return true;
        return false;
    }

    @Override
    public long getSize(View view, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(View view, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        Forward forward = new Forward(view.getView());
        MessageBodyWriter<Forward> messageBodyWriter = providers.getMessageBodyWriter(Forward.class, Forward.class, annotations, mediaType);
        messageBodyWriter.writeTo(forward,Forward.class,Forward.class,annotations,mediaType,httpHeaders,entityStream);

    }
}

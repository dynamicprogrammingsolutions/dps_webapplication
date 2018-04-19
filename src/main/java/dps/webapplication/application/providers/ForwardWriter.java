package dps.webapplication.application.providers;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.jboss.resteasy.spi.ResteasyProviderFactory.getContextData;

@Provider
@Produces(MediaType.TEXT_HTML)
public class ForwardWriter implements MessageBodyWriter<Forward> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (type.isAssignableFrom(Forward.class)) return true;
        return false;
    }

    @Override
    public long getSize(Forward s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Forward s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

        HttpServletRequest request = getContextData(HttpServletRequest.class);
        HttpServletResponse response = getContextData(HttpServletResponse.class);

        RequestDispatcher requestDispatcher = request.getRequestDispatcher(s.getTo());
        try {
            requestDispatcher.forward(request,response);
        } catch (ServletException e) {
            e.printStackTrace();
        }

    }
}

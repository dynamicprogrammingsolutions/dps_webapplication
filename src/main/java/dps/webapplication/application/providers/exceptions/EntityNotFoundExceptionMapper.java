package dps.webapplication.application.providers.exceptions;

import dps.webapplication.application.providers.View;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;

@Provider
public class EntityNotFoundExceptionMapper extends ExceptionMapperBase<EntityNotFoundException> {

    public EntityNotFoundExceptionMapper() throws NoSuchMethodException {
        super(Response.Status.NOT_FOUND);
    }

}

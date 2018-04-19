package dps.webapplication.application.providers.exceptions;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InternalServerErrorExceptionMapper extends ExceptionMapperBase<InternalServerErrorException> {

    public InternalServerErrorExceptionMapper() {
        super();
    }

}

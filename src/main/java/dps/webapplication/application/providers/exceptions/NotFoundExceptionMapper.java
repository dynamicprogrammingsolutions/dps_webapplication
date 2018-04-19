package dps.webapplication.application.providers.exceptions;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper extends ExceptionMapperBase<NotFoundException> {

    public NotFoundExceptionMapper() {
        super();
    }

}

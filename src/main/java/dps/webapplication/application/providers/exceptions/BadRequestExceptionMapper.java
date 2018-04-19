package dps.webapplication.application.providers.exceptions;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper extends ExceptionMapperBase<BadRequestException> {

    public BadRequestExceptionMapper() {
        super();
        this.sendEmpty = true;
    }

}

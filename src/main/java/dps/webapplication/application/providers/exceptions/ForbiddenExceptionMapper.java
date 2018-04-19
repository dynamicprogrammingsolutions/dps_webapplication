package dps.webapplication.application.providers.exceptions;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenExceptionMapper extends ExceptionMapperBase<ForbiddenException> {

    public ForbiddenExceptionMapper() {
        super();
    }

}

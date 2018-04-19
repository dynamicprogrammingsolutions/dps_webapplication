package dps.webapplication.application.providers.exceptions;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.ext.Provider;

@Provider
public class RedirectionExceptionMapper extends ExceptionMapperBase<RedirectionException> {

    public RedirectionExceptionMapper() {
        super();
    }

}

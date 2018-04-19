package dps.webapplication.application.providers.exceptions;

import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.ext.Provider;

@Provider
public class NotAcceptableExceptionMapper extends ExceptionMapperBase<NotAcceptableException> {

    public NotAcceptableExceptionMapper() {
        super();
        this.sendEmpty = true;
    }

}

package dps.webapplication.application.providers.exceptions;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.ext.Provider;

@Provider
public class NotAllowedExceptionMapper extends ExceptionMapperBase<NotAuthorizedException> {

    public NotAllowedExceptionMapper() {
        super();
        this.sendEmpty = true;
    }

}

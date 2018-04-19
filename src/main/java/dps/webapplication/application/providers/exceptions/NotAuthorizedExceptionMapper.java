package dps.webapplication.application.providers.exceptions;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.ext.Provider;

@Provider
public class NotAuthorizedExceptionMapper extends ExceptionMapperBase<NotAllowedException> {

    public NotAuthorizedExceptionMapper() {
        super();
        this.sendEmpty = true;
    }

}

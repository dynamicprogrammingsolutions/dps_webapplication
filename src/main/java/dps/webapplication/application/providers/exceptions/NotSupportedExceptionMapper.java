package dps.webapplication.application.providers.exceptions;

import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.ext.Provider;

@Provider
public class NotSupportedExceptionMapper extends ExceptionMapperBase<NotSupportedException> {

    public NotSupportedExceptionMapper() {
        super();
        this.sendEmpty = true;
    }

}

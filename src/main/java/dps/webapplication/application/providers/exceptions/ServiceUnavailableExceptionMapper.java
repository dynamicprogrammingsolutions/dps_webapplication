package dps.webapplication.application.providers.exceptions;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.ext.Provider;

@Provider
public class ServiceUnavailableExceptionMapper extends ExceptionMapperBase<ServiceUnavailableException> {

    public ServiceUnavailableExceptionMapper() {
        super();
    }

}

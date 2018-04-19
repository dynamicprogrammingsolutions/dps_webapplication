package dps.webapplication.application.providers.exceptions;

import dps.commons.reflect.ReflectHelper;
import dps.logging.HasLogger;
import dps.webapplication.application.providers.View;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.logging.Level;

public class ExceptionMapperBase<E extends Throwable> implements ExceptionMapper<E>, HasLogger {

    @Inject
    HttpServletRequest request;

    protected Class<E> exceptionClass;
    protected Response.Status.Family statusFamily;
    protected Response.Status resultStatus;
    protected Level logLevel;
    protected boolean sendEmpty = false;
    protected String errorPage = null;

    public ExceptionMapperBase(Response.Status resultStatus, Response.Status.Family statusFamily, Level logLevel) {
        exceptionClass = ReflectHelper.getTypeParameter(this.getClass(),0);
        if (resultStatus == null) {
            if (!WebApplicationException.class.isAssignableFrom(exceptionClass)) {
                throw new IllegalArgumentException("Result Status not given for exception class that is not subclass of WebApplicationException");
            }
            this.statusFamily = statusFamily;
        } else {
            this.resultStatus = resultStatus;
            this.statusFamily = resultStatus.getFamily();
        }
        if (logLevel != null) this.logLevel = logLevel;
        else {
            if (this.statusFamily != null) {
                this.logLevel = getLogLevel(this.statusFamily);
            }
        }
    }

    public ExceptionMapperBase(Response.Status resultStatus) {
        this(resultStatus,null,null);
    }

    public ExceptionMapperBase(Response.Status.Family statusFamily) {
        this(null,statusFamily,null);
    }

    public ExceptionMapperBase(Level logLevel) {
        this(null,null,logLevel);
    }

    public ExceptionMapperBase() {
        this(null,null,null);
    }

    public Response toResponse(E exception)
    {

        Level level;
        int status;
        String reasonPhrase;

        Response response = null;

        if (logLevel == null || resultStatus == null) {
            if (exception instanceof WebApplicationException) {
                WebApplicationException webApplicationException = (WebApplicationException)exception;
                Response.Status.Family family;
                response = webApplicationException.getResponse();
                if (resultStatus == null) {
                    Response.StatusType statusInfo = response.getStatusInfo();
                    status = statusInfo.getStatusCode();
                    reasonPhrase = statusInfo.getReasonPhrase();
                    family = statusInfo.getFamily();
                } else {
                    status = resultStatus.getStatusCode();
                    reasonPhrase = resultStatus.getReasonPhrase();
                    family = resultStatus.getFamily();
                }
                if (logLevel == null) {
                    level = getLogLevel(family);
                } else {
                    level = logLevel;
                }
            } else {
                throw new IllegalArgumentException("Result Status or Log Level not given for exception class that is not subclass of WebApplicationException");
            }
        } else {
            level = logLevel;
            status = resultStatus.getStatusCode();
            reasonPhrase = resultStatus.getReasonPhrase();
        }


        if (exception instanceof RedirectionException) {
            RedirectionException redirectionException = (RedirectionException) exception;
            if (response == null) {
                response = redirectionException.getResponse();
            }
            logException(exception,level,status,reasonPhrase,"redirect: "+redirectionException.getLocation());
            return response;
        }

        if (sendEmpty) {
            logException(exception,level,status,reasonPhrase,"empty response");
            return Response.status(status).build();
        } else {
            String errorPage = "/WEB-INF/" + (this.errorPage != null ? this.errorPage : String.valueOf(status)) + ".jsp";
            logException(exception,level,status,reasonPhrase,"error page: "+errorPage);
            if (errorPage == "error") {
                request.setAttribute("statusCode", status);
                request.setAttribute("reasonPhrase", reasonPhrase);
            }
            return Response.status(status).type(MediaType.TEXT_HTML).entity(new View(errorPage)).build();
        }
    }

    void logException(Throwable exception, Level level, int status, String reasonPhrase, String additionalInfo) {
        Throwable cause = exception.getCause();
        log(level,status+" "+reasonPhrase+" "+request.getMethod()+" "+request.getRequestURI()+" "+exception+(cause!=null?" cause: "+cause:"")+" "+additionalInfo);
    }

    Level getLogLevel(Response.Status.Family statusFamily)
    {
        switch (statusFamily) {
            case REDIRECTION:
                return Level.INFO;
            case CLIENT_ERROR:
                return Level.WARNING;
            case SERVER_ERROR:
                return Level.SEVERE;
            default:
                return Level.INFO;
        }
    }

}

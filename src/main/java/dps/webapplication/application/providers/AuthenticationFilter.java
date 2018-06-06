package dps.webapplication.application.providers;

import dps.commons.reflect.ReflectHelper;
import dps.webapplication.application.providers.annotations.AllowedRoles;
import dps.webapplication.application.providers.annotations.NotAuthorizedRedirect;
import dps.webapplication.authentication.CurrentAuthenticationManager;
import dps.webapplication.configuration.Settings;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    @Inject
    CurrentAuthenticationManager authenticationManager;

    @Context
    private HttpServletRequest request;

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    Settings settings;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        Method resourceMethod = resourceInfo.getResourceMethod();
        NotAuthorizedRedirect redirectAnnotation = resourceMethod.getAnnotation(NotAuthorizedRedirect.class);

        List<Object> matchedResources = requestContext.getUriInfo().getMatchedResources();
        for (Object resource: matchedResources) {
            Class<?> resourceClass = resource.getClass();
            AllowedRoles roles = ReflectHelper.getAnnotation(resourceClass,AllowedRoles.class);
            if (roles == null) return;
            if (!authenticationManager.isAuthenticated()) {
                if (redirectAnnotation != null) {

                    Object originalRequest = request.getAttribute("self");
                    if (originalRequest != null) {
                        request.getSession().setAttribute("originalRequest", originalRequest);
                    }

                    try {
                        throw new RedirectionException(Response.Status.TEMPORARY_REDIRECT,new URI(request.getAttribute("requestedHost")+settings.getRoot()+redirectAnnotation.value()));
                    } catch (URISyntaxException e) {
                        throw new WebApplicationException("invalid redirection url");
                    }
                }
                throw new NotAuthorizedException(Response.status(401).build());
            }
            if (!authenticationManager.isAuthorized(roles.value())) {
                throw new ForbiddenException();
            }
        }
    }

}

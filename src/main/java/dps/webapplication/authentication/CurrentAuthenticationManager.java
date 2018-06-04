package dps.webapplication.authentication;

import dps.authentication.AuthenticableUser;
import dps.authentication.AuthenticationManager;
import dps.authentication.AuthenticationManagerFactory;
import dps.webapplication.servlets.SessionOfRequest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

@Named("auth")
@RequestScoped
public class CurrentAuthenticationManager implements AuthenticationManager {

    @Inject AuthenticationManagerFactory authenticationManagerFactory;
    @Inject SessionOfRequest sessionOfRequest;
    AuthenticationManager authenticationManager;

    @PostConstruct
    void init() {
        HttpSession httpSession = sessionOfRequest.getSession();
        authenticationManager = authenticationManagerFactory.getAuthenticationManager(httpSession);
    }

    @Override
    public Boolean login(String username, String password) {
        return authenticationManager.login(username, password);
    }

    @Override
    public Boolean login(String token) {
        return authenticationManager.login(token);
    }

    @Override
    public AuthenticableUser getUser() {
        return authenticationManager.getUser();
    }

    @Override
    public String getToken() {
        return authenticationManager.getToken();
    }

    @Override
    public void logout() {
        authenticationManager.logout();
    }

    @Override
    public Boolean isAuthenticated() {
        return authenticationManager.isAuthenticated();
    }

    @Override
    public Boolean isAuthorized(String operation) {
        return authenticationManager.isAuthorized(operation);
    }
}

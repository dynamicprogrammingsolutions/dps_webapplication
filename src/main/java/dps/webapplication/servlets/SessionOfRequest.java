package dps.webapplication.servlets;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpSession;

@RequestScoped
public class SessionOfRequest {
    HttpSession session;

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }
}

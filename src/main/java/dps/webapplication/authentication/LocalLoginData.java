package dps.webapplication.authentication;

import dps.authentication.AuthenticableUser;
import dps.authentication.LoginData;

import java.io.Serializable;
import java.util.Date;

public class LocalLoginData implements Serializable, LoginData {

    private static final long serialVersionUID = -8247342200571844968L;

    private String key;
    private String token;
    private AuthenticableUser user;
    private Date loginTime;
    private Date tokenIssueTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthenticableUser getUser() {
        return user;
    }

    public void setUser(AuthenticableUser user) {
        this.user = user;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getTokenIssueTime() {
        return tokenIssueTime;
    }

    public void setTokenIssueTime(Date tokenIssueTime) {
        this.tokenIssueTime = tokenIssueTime;
    }
}

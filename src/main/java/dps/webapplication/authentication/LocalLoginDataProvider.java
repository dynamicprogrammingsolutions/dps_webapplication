package dps.webapplication.authentication;

import dps.authentication.AuthenticableUser;
import dps.authentication.LoginData;
import dps.authentication.LoginDataProvider;

import javax.enterprise.context.ApplicationScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class LocalLoginDataProvider implements Serializable, LoginDataProvider {

    private static final long serialVersionUID = -2470696938023264627L;

    Map<String,LocalLoginData> logins = new HashMap<>();

    @Override
    public LoginData addToken(String key, String token, AuthenticableUser user) {
        LocalLoginData loginData = new LocalLoginData();
        loginData.setKey(key);
        loginData.setLoginTime(new Date());
        loginData.setKey(key);
        loginData.setToken(token);
        loginData.setTokenIssueTime(new Date());
        loginData.setUser(user);
        logins.put(key,loginData);
        return loginData;
    }

    @Override
    public void UpdateLoginData(LoginData loginData) {
        LocalLoginData foundLoginData = logins.get(loginData.getKey());
        if (loginData instanceof LocalLoginData && foundLoginData != null) {
            logins.put(loginData.getKey(),(LocalLoginData)loginData);
        }
    }

    @Override
    public LoginData getLoginData(String key) {
        return logins.get(key);
    }
}

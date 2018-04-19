package dps.webapplication.authentication;

import dps.authentication.AuthenticableUser;
import dps.authentication.UserDataProvider;
import dps.commons.startup.Startup;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.io.Serializable;

@ApplicationScoped
@Startup
public class AuthenticableUserProvider implements Serializable, UserDataProvider {

    @PersistenceContext(unitName = "DefaultPersistenceUnit")
    EntityManager em;

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public AuthenticableUser getUserByCredentials(String username, String password) {
        TypedQuery<? extends AuthenticableUser> query = em.createNamedQuery("ApplicationUser.getByUsername", AuthenticableUser.class);
        query.setParameter("username",username);
        try {
            AuthenticableUser user = query.getSingleResult();
            if (user.checkCredentials(username,password)) {
                return user;
            } else {
                return null;
            }
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Boolean checkAuthorization(AuthenticableUser authenticableUser, String s) {
        return true;
    }

}

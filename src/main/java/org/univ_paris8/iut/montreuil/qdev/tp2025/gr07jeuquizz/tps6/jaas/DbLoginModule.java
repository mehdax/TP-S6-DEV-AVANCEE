package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.jaas;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO.UserDAO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * JAAS LoginModule pour l'authentification username/password via la base de
 * données.
 *
 * Flow :
 * 1. initialize() → reçoit Subject, CallbackHandler et options
 * 2. login() → collecte credentials via callbacks, vérifie en BDD
 * 3. commit() → ajoute UserPrincipal + RolePrincipal au Subject
 * 4. abort() / logout() → nettoie le Subject
 *
 * Utilisé dans : LoginContext("MasterAnnonceLogin", callbackHandler)
 */
public class DbLoginModule implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;

    private boolean loginSucceeded = false;
    private String username;
    private Long userId;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
            Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public boolean login() throws LoginException {
        // Collecte des credentials via callbacks
        NameCallback nameCallback = new NameCallback("Username: ");
        PasswordCallback passwordCallback = new PasswordCallback("Password: ", false);

        try {
            callbackHandler.handle(new Callback[] { nameCallback, passwordCallback });
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Erreur lors de la collecte des credentials : " + e.getMessage());
        }

        String name = nameCallback.getName();
        String password = new String(passwordCallback.getPassword());
        passwordCallback.clearPassword();

        // Vérification en base de données
        EntityManager em = JPAUtil.getEntityManager();
        try {
            UserDAO userDAO = new UserDAO(em);
            Optional<User> userOpt = userDAO.authenticate(name, password);

            if (userOpt.isEmpty()) {
                loginSucceeded = false;
                throw new FailedLoginException("Identifiants invalides pour l'utilisateur : " + name);
            }

            this.username = userOpt.get().getUsername();
            this.userId = userOpt.get().getId();
            loginSucceeded = true;
            return true;

        } finally {
            em.close();
        }
    }

    @Override
    public boolean commit() throws LoginException {
        if (!loginSucceeded) {
            return false;
        }
        // Ajout des principals au Subject
        subject.getPrincipals().add(new UserPrincipal(username, userId));
        subject.getPrincipals().add(new RolePrincipal("ROLE_USER"));
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        loginSucceeded = false;
        username = null;
        userId = null;
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().removeIf(p -> p instanceof UserPrincipal || p instanceof RolePrincipal);
        loginSucceeded = false;
        return true;
    }
}

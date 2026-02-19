package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.jaas;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.security.TokenStore;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * JAAS LoginModule pour l'authentification par token sur chaque requête.
 *
 * Flow :
 * 1. initialize() → reçoit Subject et CallbackHandler
 * 2. login() → collecte le token via NameCallback, valide via TokenStore
 * 3. commit() → ajoute UserPrincipal + RolePrincipal au Subject
 *
 * Utilisé dans : LoginContext("MasterAnnonceToken", callbackHandler)
 * Le callbackHandler doit fournir le token via getName() du NameCallback.
 */
public class TokenLoginModule implements LoginModule {

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
        // Récupération du token via NameCallback (réutilisé comme transport de token)
        NameCallback tokenCallback = new NameCallback("Token: ");

        try {
            callbackHandler.handle(new Callback[] { tokenCallback });
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Erreur lors de la récupération du token : " + e.getMessage());
        }

        String token = tokenCallback.getName();

        // Validation via TokenStore
        Optional<TokenStore.TokenInfo> tokenInfo = TokenStore.getInstance().validate(token);

        if (tokenInfo.isEmpty()) {
            loginSucceeded = false;
            throw new FailedLoginException("Token invalide ou expiré.");
        }

        this.username = tokenInfo.get().getUsername();
        this.userId = tokenInfo.get().getUserId();
        loginSucceeded = true;
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        if (!loginSucceeded)
            return false;

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

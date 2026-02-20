package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.util.Map;
public class TokenLoginModule implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;

    
    private boolean loginSucceeded = false;
    private boolean commitSucceeded = false;

    
    private Long userId;
    private String username;

    
    private UserPrincipal userPrincipal;
    private RolePrincipal rolePrincipal;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
            Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public boolean login() throws LoginException {
        if (callbackHandler == null) {
            throw new LoginException("CallbackHandler non fourni");
        }

        
        
        NameCallback tokenCallback = new NameCallback("Token : ");

        try {
            callbackHandler.handle(new Callback[] { tokenCallback });
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Erreur lors de la récupération du token : " + e.getMessage());
        }

        String token = tokenCallback.getName();

        
        TokenStore tokenStore = TokenStore.getInstance();

        if (!tokenStore.isValid(token)) {
            loginSucceeded = false;
            throw new LoginException("Token invalide ou expiré");
        }

        
        this.userId = tokenStore.getUserId(token);
        this.username = tokenStore.getUsername(token);
        loginSucceeded = true;
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        if (!loginSucceeded) {
            return false;
        }

        userPrincipal = new UserPrincipal(username, userId);
        rolePrincipal = new RolePrincipal("ROLE_USER");

        subject.getPrincipals().add(userPrincipal);
        subject.getPrincipals().add(rolePrincipal);

        commitSucceeded = true;
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        if (!loginSucceeded) {
            return false;
        }
        if (commitSucceeded) {
            logout();
        }
        loginSucceeded = false;
        commitSucceeded = false;
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        if (userPrincipal != null) {
            subject.getPrincipals().remove(userPrincipal);
        }
        if (rolePrincipal != null) {
            subject.getPrincipals().remove(rolePrincipal);
        }
        userPrincipal = null;
        rolePrincipal = null;
        userId = null;
        username = null;
        loginSucceeded = false;
        commitSucceeded = false;
        return true;
    }
}


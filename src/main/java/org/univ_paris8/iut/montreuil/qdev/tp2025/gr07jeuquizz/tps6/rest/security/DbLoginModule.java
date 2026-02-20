package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.model.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.service.UserService;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
public class DbLoginModule implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;

    
    private boolean loginSucceeded = false;
    private boolean commitSucceeded = false;
    private User authenticatedUser = null;

    
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

        
        NameCallback nameCallback = new NameCallback("Username : ");
        PasswordCallback passwordCallback = new PasswordCallback("Password : ", false);

        try {
            callbackHandler.handle(new Callback[] { nameCallback, passwordCallback });
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Erreur lors de la récupération des credentials : " + e.getMessage());
        }

        String username = nameCallback.getName();
        char[] passwordChars = passwordCallback.getPassword();
        String password = (passwordChars != null) ? new String(passwordChars) : "";

        
        passwordCallback.clearPassword();

        
        UserService userService = new UserService();
        Optional<User> userOpt = userService.authenticate(username, password);

        if (userOpt.isPresent()) {
            authenticatedUser = userOpt.get();
            loginSucceeded = true;
            return true;
        } else {
            loginSucceeded = false;
            throw new LoginException("Identifiants invalides pour l'utilisateur : " + username);
        }
    }
    @Override
    public boolean commit() throws LoginException {
        if (!loginSucceeded) {
            return false;
        }

        
        userPrincipal = new UserPrincipal(
                authenticatedUser.getUsername(),
                authenticatedUser.getId());
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

        
        authenticatedUser = null;
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
        authenticatedUser = null;
        loginSucceeded = false;
        commitSucceeded = false;
        return true;
    }
}


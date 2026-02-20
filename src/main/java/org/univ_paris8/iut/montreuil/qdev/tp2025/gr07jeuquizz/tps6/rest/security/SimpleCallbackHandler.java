package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security;

import javax.security.auth.callback.*;
import java.io.IOException;
public class SimpleCallbackHandler implements CallbackHandler {

    private final String username;
    private final char[] password;

    public SimpleCallbackHandler(String username, String password) {
        this.username = username;
        this.password = password != null ? password.toCharArray() : new char[0];
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                ((NameCallback) callback).setName(username);
            } else if (callback instanceof PasswordCallback) {
                ((PasswordCallback) callback).setPassword(password);
            } else {
                throw new UnsupportedCallbackException(callback,
                        "Callback non supporté : " + callback.getClass().getName());
            }
        }
    }
}


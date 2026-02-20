package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@Tag("unit")
class TokenLoginModuleTest {

    private TokenLoginModule loginModule;
    private Subject subject;

    @Mock
    private CallbackHandler callbackHandler;

    private TokenStore tokenStore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginModule = new TokenLoginModule();
        subject = new Subject();
        tokenStore = TokenStore.getInstance();

        // Initialisation JAAS
        loginModule.initialize(subject, callbackHandler, new HashMap<>(), new HashMap<>());
    }

    @Test
    void testLoginSuccess() throws Exception {
        // GIVEN
        long userId = 100L;
        String username = "validUser";
        String token = tokenStore.generateToken(userId, username);

        // Configurer le mock pour retourner le token via NameCallback
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Callback[] callbacks = (Callback[]) invocation.getArguments()[0];
                for (Callback cb : callbacks) {
                    if (cb instanceof NameCallback) {
                        ((NameCallback) cb).setName(token);
                    }
                }
                return null;
            }
        }).when(callbackHandler).handle(any(Callback[].class));

        // WHEN
        assertTrue(loginModule.login(), "Login should succeed");
        assertTrue(loginModule.commit(), "Commit should succeed");

        // THEN
        Set<UserPrincipal> principals = subject.getPrincipals(UserPrincipal.class);
        assertFalse(principals.isEmpty(), "Subject should contain UserPrincipal");
        UserPrincipal principal = principals.iterator().next();
        assertEquals(userId, principal.getUserId());
        assertEquals(username, principal.getName());
    }

    @Test
    void testLoginFailure_InvalidToken() throws Exception {
        // GIVEN
        String invalidToken = "invalid-token";

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Callback[] callbacks = (Callback[]) invocation.getArguments()[0];
                for (Callback cb : callbacks) {
                    if (cb instanceof NameCallback) {
                        ((NameCallback) cb).setName(invalidToken);
                    }
                }
                return null;
            }
        }).when(callbackHandler).handle(any(Callback[].class));

        // WHEN / THEN
        assertThrows(LoginException.class, () -> loginModule.login());
    }
}

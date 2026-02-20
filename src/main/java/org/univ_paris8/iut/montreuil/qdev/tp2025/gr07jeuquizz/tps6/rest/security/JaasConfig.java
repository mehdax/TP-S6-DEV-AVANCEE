package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.rest.security;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.net.URL;
@WebListener
public class JaasConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        URL jaasConfigUrl = Thread.currentThread()
                .getContextClassLoader()
                .getResource("jaas.conf");

        if (jaasConfigUrl != null) {
            System.setProperty("java.security.auth.login.config", jaasConfigUrl.toString());
            System.out.println("[JAAS] Configuration chargée : " + jaasConfigUrl);
        } else {
            System.err.println("[JAAS] ATTENTION : jaas.conf introuvable dans le classpath !");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
    }
}


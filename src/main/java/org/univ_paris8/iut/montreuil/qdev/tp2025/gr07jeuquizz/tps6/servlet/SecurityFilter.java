package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.servlet;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/AnnonceAdd", "/AnnonceUpdate", "/AnnonceDelete"})
public class SecurityFilter implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {
        // Initialization (if needed)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Vérifier l'authentification
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            // Rediriger vers la page de login
            httpResponse.sendRedirect("login");
            return;
        }

        // L'utilisateur est authentifié, continuer
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup (if needed)
    }
}

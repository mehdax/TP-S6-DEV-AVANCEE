package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");

        String nom = request.getParameter("nom");

        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Hello the World " + nom + "</h1>");
        out.println("<br><a href='form.jsp'>Retour</a>");
        out.println("</body></html>");
    }

    public void destroy() {
    }
}

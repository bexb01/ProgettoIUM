package com.example.Progetto;

import DAO.Model;
import DAO.Utente;

import java.io.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

/*-----------------------ESEMPIO (Utilizzabile)-----------------------*/
@WebServlet(name = "loginServlet", value = "/login-servlet")
public class LoginServlet extends HttpServlet {

    private Model model = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext ctx = getServletContext();
        model = (Model) ctx.getAttribute("DAO");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        String username = request.getParameter("login");
        String password = request.getParameter("passwd");
        HttpSession s = request.getSession();

        if (username != null && password!=null){
            Utente utente = model.getUtente(username, password);
            if (utente != null) {
                s.setAttribute("username", username);
                if(utente.getAmministratore())
                    s.setAttribute("ruolo", "amministratore");
                else
                    s.setAttribute("ruolo", "utente");
                if (s.getAttribute("ruolo").equals("amministratore")){
                    // Effettua l'inserimento dei dati del docente
                    //!!!NON è COMPLETO!!!
                    //DOESN'T WORK--------------------------------------------------------------------------
                    String nomeDocente = request.getParameter("name");
                    String cognomeDocente = request.getParameter("surname");
                    model.insertDocente(nomeDocente,cognomeDocente);
                    try (PrintWriter out = response.getWriter()) {
                        out.println("<!DOCTYPE html>");
                        out.println("<html>");
                        out.println("<head>");
                        out.println("<title>Feedback positivo</title>");
                        out.println("</head>");
                        out.println("<body>");
                        out.println("<p>Ciao " + s.getAttribute("username") + "</p>");
                        out.println("<p>Hai ruolo " + s.getAttribute("ruolo") + "</p>");
                        out.println("<p>Operazione completata con successo.</p>");
                        out.println("</body>");
                        out.println("</html>");
                        return;
                    }
                    //----------------------------------------------------------------------------------
                }
            }else{
                s.setAttribute("ruolo", "fail");
            }
        }
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet GestisciSessione</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<p>Ciao ");
            out.println(s.getAttribute("username") + "</p>");
            out.println("<p>hai ruolo " + s.getAttribute("ruolo") + "</p>");
            out.println("<p>non essendo un amministratore l'operazione è fallita</p>");
            out.println("<p>Ricarica la pagina: ");
            out.println("<form action=\"login-servlet\" method=\"post\">"
                    + "   <input type=\"submit\" name=\"submit\" value=\"OK\"/>"
                    + "</form> </p>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    public void destroy() {
    }
}
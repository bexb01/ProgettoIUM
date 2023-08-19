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

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        String username = request.getParameter("login");
        String password = request.getParameter("passwd");
        HttpSession s = request.getSession();

        if (username != null && password!=null){
            Utente utente = model.getUtente(username, password);
            if (utente != null) {
                s.setAttribute("username", username);
                if(utente.getAmministratore()) {
                    s.setAttribute("ruolo", "amministratore");

                } else {
                    s.setAttribute("ruolo", "utente");
                }
                    //----------------------------------------------------------------------------------
                try (PrintWriter out = response.getWriter()) {
                    out.println("Welcome back");
                }
            }else{
                //di norma 200
                //fail log in 401 (non autorizzato)
                response.setStatus(401);
                //s.setAttribute("ruolo", "fail");
            }
        }

    }

    public void destroy() {
    }
}
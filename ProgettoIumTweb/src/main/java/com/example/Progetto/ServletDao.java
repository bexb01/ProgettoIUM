package com.example.Progetto;

import DAO.*;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

/*-----------------------CLASSE DA COLLEGARE A RESTO DEL PROGETTO (UTILE)-----------------------*/
@WebServlet(name = "ServletDao", value = "/ServletDao", loadOnStartup = 1, asyncSupported = true)
public class ServletDao extends HttpServlet {

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext ctx = getServletContext();
        String url = ctx.getInitParameter("DB-URL");
        String user = ctx.getInitParameter("user");
        String pwd = ctx.getInitParameter("pwd");
        System.out.println("URL del database: " + url);
        Model model = new Model(url, user, pwd);
        //setto il dao come attributo nella servlet context cosi le altre servlet possono accedervi (accedono tutte allo stesso dao)
        ctx.setAttribute("DAO", model);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    }

    public void destroy() {
    }

}

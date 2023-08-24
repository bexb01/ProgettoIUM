package com.example.Progetto;

import DAO.*;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "ServletDao", value = "/ServletDao", loadOnStartup = 1, asyncSupported = true)
public class ServletDao extends HttpServlet {

    @Override
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    }

    @Override
    public void destroy() {
    }

}

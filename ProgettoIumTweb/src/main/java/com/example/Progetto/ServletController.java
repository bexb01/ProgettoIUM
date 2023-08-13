package com.example.Progetto;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/*-----------------------ESEMPIO (SERVIRA IN FUTURO)-----------------------*/
@WebServlet(name = "ServletController", value = "/ServletController")
public class ServletController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // per essere robusti rispetto a caratteri speciali (', etc)
        ServletContext ctx = getServletContext();
        String action = request.getParameter("action");
        RequestDispatcher rd = ctx.getRequestDispatcher("/index.html");
        if (action!=null) {
            switch (action) {
                case "home":
                    rd = ctx.getRequestDispatcher("/index.html");
                    break;
                case "pageA":
                    rd = ctx.getRequestDispatcher("/A.html");
                    break;
                case "pageB":
                    rd = ctx.getRequestDispatcher("/B.html");
                    break;
                default:
            }
            rd.forward(request, response);
        }
    }
}

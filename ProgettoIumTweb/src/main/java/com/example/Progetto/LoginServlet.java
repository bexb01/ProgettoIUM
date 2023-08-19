package com.example.Progetto;

import DAO.Model;
import DAO.Utente;
import org.json.JSONObject;
import utils.JsonUtils;

import java.io.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

/*-----------------------ESEMPIO (Utilizzabile)-----------------------*/
@WebServlet(name = "loginServlet", value = "/loginServlet")
public class LoginServlet extends HttpServlet {

    private Model model = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext ctx = getServletContext();
        model = (Model) ctx.getAttribute("DAO");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = JsonUtils.readJson(request);
        String username = jsonObject.getString("login");
        String password = jsonObject.getString("passwd");
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
                out.println("Welcome back");
                out.flush();
            }else{
                //di norma 200
                //fail log in 401 (non autorizzato)
                response.setStatus(401);
                out.println("Non sei autorizzato");
                out.flush();
                //s.setAttribute("ruolo", "fail");
            }
        } else {
            response.setStatus(404);
            out.println("Parametri mancanti");
            out.flush();
        }



    }

    public void destroy() {
    }
}
package com.example.Progetto;

import DAO.Model;
import org.json.JSONObject;
import utils.JsonUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "PrenotazioniServlet", value = "/PrenotazioniServlet")
public class PrenotazioniServlet extends HttpServlet {

    private Model model = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext ctx = getServletContext();
        model = (Model) ctx.getAttribute("DAO");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = JsonUtils.readJson(request);
        String action = jsonObject.getString("action");
        HttpSession s = request.getSession();
        String ruolo = (String) s.getAttribute("ruolo");

        if(ruolo.equals("amministratore")){
            switch (action){
                case "":
                    break;
                default:
                    response.setStatus(404);
                    out.println("Errore nella richiesta");
                    out.flush();
                    break;
            }
        } else {
            response.setStatus(401);
            out.println("Non sei autorizzato");
            out.flush();
        }
    }

    @Override
    public void destroy() {
    }
}

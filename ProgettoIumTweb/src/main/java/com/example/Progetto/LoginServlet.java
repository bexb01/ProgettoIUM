package com.example.Progetto;

import DAO.Model;
import utils.UserValidationResult;
import DAO.Utente;
import org.json.JSONObject;
import utils.JsonUtils;

import java.io.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "loginServlet", value = "/loginServlet")
public class LoginServlet extends HttpServlet {

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
            processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = JsonUtils.readJson(request);
        String action = jsonObject.getString("action");
        JSONObject r = new JSONObject();
        if (action != null) {
            switch (action) {
                case "login":
                    System.out.println(jsonObject);
                    if (!jsonObject.optString("email").isEmpty() && !jsonObject.optString("passwd").isEmpty()) {
                        UserValidationResult validationResult = model.getUtente(jsonObject.getString("email"), jsonObject.getString("passwd"));
                        if (validationResult.getUtente() != null) {
                            Utente utente = validationResult.getUtente();
                            HttpSession s = request.getSession();
                            s.setAttribute("id_utente", utente.getId_utente());
                            s.setAttribute("ruolo", utente.getRuolo());
                            r.put("id_utente", utente.getId_utente());
                            r.put("nome", utente.getNome());
                            r.put("cognome", utente.getCognome());
                            r.put("ruolo", utente.getRuolo());
                            r.put("messaggio", "Autenticazione avvenuta con successo");
                        } else {
                            response.setStatus(401);
                            r.put("messaggio", validationResult.getErrorMessage());
                        }
                    } else {
                        response.setStatus(404);
                        r.put("messaggio", "Parametri mancanti");
                    }
                    break;
                case "sign up":
                    String nome = jsonObject.optString("nome");
                    String cognome = jsonObject.optString("cognome");
                    String email = jsonObject.optString("email");
                    String passwd = jsonObject.optString("passwd");
                    if (!email.isEmpty() && !nome.isEmpty() && !cognome.isEmpty() && passwd.length() >= 6) {
                        UserValidationResult validationResult = model.insertUtente(jsonObject.getString("nome"), jsonObject.getString("cognome"), jsonObject.getString("email"), jsonObject.getString("passwd"));
                        if (validationResult.getUtente() != null){
                            Utente utente = validationResult.getUtente();
                            HttpSession s = request.getSession();
                            s.setAttribute("username", utente.getId_utente());
                            s.setAttribute("ruolo", utente.getRuolo());
                            r.put("id_utente", utente.getId_utente());
                            r.put("nome", utente.getNome());
                            r.put("ruolo", utente.getRuolo());
                            r.put("messaggio", "Registrazione avvenuta con successo");
                        } else {
                            //ERRORE INTERNO RIPROVA PIù TARDI O CONTATTA IL SUPPORTO
                            response.setStatus(500);
                            r.put("messaggio", validationResult.getErrorMessage());
                        }
                    } else {
                        response.setStatus(404);
                        r.put("messaggio", "Parametri mancanti");
                    }
                    break;
                case "logout":
                    HttpSession s = request.getSession(false);
                    s.invalidate();
                    r.put("messaggio", "Logout effettuato con successo");
                    break;
                default:
                    response.setStatus(404);
                    r.put("messaggio", "Errore nella richiesta");
                    break;
            }
            out.println(r);
            out.flush();
        }
    }

    @Override
    public void destroy() {
    }
}
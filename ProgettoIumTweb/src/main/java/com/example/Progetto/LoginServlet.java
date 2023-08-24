package com.example.Progetto;

import DAO.Model;
import DAO.Utente;
import org.json.JSONObject;
import utils.JsonUtils;

import java.io.*;
import java.sql.SQLException;
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
        if (action != null) {
            switch (action) {
                case "login":
                    System.out.println(jsonObject);
                    if (!jsonObject.optString("email").isEmpty() && !jsonObject.optString("passwd").isEmpty()) {
                        Utente utente = model.getUtente(jsonObject.getString("email"), jsonObject.getString("passwd"));
                        if (utente != null) {
                            HttpSession s = request.getSession();
                            s.setAttribute("id_utente", utente.getId_utente());
                            s.setAttribute("ruolo", utente.getRuolo());
                            JSONObject r = new JSONObject();
                            r.put("id_utente", utente.getId_utente());
                            r.put("nome", utente.getNome());
                            r.put("ruolo", utente.getRuolo());
                            r.put("messaggio", "Autenticazione avvenuta con successo");
                            out.println(response);
                            out.flush();
                        } else {
                            response.setStatus(401);
                            JSONObject r = new JSONObject();
                            r.put("messaggio", "L'email o la password non sono corretti");
                            out.println(response);
                            out.flush();
                        }
                    } else {
                        response.setStatus(404);
                        out.println("Parametri mancanti");
                        out.flush();
                    }
                    break;
                case "sign up":
                    String nome = jsonObject.optString("nome");
                    String cognome = jsonObject.optString("cognome");
                    String email = jsonObject.optString("email");
                    String passwd = jsonObject.optString("passwd");
                    if (!email.isEmpty() && !nome.isEmpty() && !cognome.isEmpty() && passwd.length() >= 6) {
                        Utente utente = new Utente(nome, cognome, email, passwd);
                        int id = model.insertUtente(utente.getNome(), utente.getCognome(), utente.getEmail(), utente.getPassword());
                        if(id > 0){
                            HttpSession s = request.getSession();
                            s.setAttribute("username", utente.getId_utente());
                            s.setAttribute("ruolo", utente.getRuolo());
                            JSONObject r = new JSONObject();
                            r.put("id_utente", utente.getId_utente());
                            r.put("nome", utente.getNome());
                            r.put("ruolo", utente.getRuolo());
                            r.put("messaggio", "Registrazione avvenuta con successo");
                            out.println(response);
                            out.flush();
                        } else {
                            response.setStatus(500);
                            JSONObject r = new JSONObject();
                            r.put("messaggio", "Errore interno al server, ritenta la registrazione più tardi o contatta il supporto");
                            out.println(response);
                            out.flush();
                        }
                    } else {
                        response.setStatus(404);
                        out.println("Parametri mancanti");
                        out.flush();
                    }
                    break;
                case "logout":
                    HttpSession s = request.getSession(false);
                    s.invalidate();
                    JSONObject r = new JSONObject();
                    r.put("messaggio", "Logout effettuato con successo");
                    out.println(response);
                    out.flush();
                    break;
                default:
                    response.setStatus(404);
                    out.println("Errore nella richiesta");
                    out.flush();
                    break;
            }
        }
    }

    @Override
    public void destroy() {
    }
}
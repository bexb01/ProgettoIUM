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
        System.out.println("mannaggia sant antonio");
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
                        System.out.println("1");
                        Utente utente = model.getUtente(jsonObject.getString("email"), jsonObject.getString("passwd"));
                        System.out.println("2");
                        if (utente != null) {
                            System.out.println("3");
                            HttpSession s = request.getSession();
                            System.out.println("4");
                            s.setAttribute("id_utente", utente.getId_utente());
                            System.out.println("5");
                            s.setAttribute("ruolo", utente.getRuolo());
                            System.out.println("6");
                            JSONObject r = new JSONObject();
                            System.out.println("7");
                            r.put("idUser", utente.getId_utente());
                            System.out.println("8");
                            r.put("nome", utente.getNome());
                            System.out.println("9");
                            r.put("ruolo", utente.getRuolo());
                            System.out.println("10");
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
                        model.insertUtente(utente.getNome(), utente.getCognome(), utente.getEmail(), utente.getPassword(), false);
                        //chiedere cosa fa in insert utente per tornare oggetto utente AGGIUNGERE IF----------------
                        HttpSession s = request.getSession();
                        s.setAttribute("username", utente.getId_utente());
                        s.setAttribute("ruolo", utente.getRuolo());
                        JSONObject r = new JSONObject();
                        r.put("id_utente", utente.getId_utente());
                        r.put("nome", utente.getNome());
                        r.put("ruolo", utente.getRuolo());
                        r.put("messaggio", "Registrazione avvenuta con successo");
                        out.println(response);
                        out.flush();//----------------------------------------------------------------------------
                        //AGGIUNGERE ELSE
                        /*else {
                            res.setStatus(500);
                            out.println("Errore interno al server, ritenta la registrazione più tardi o contatta il supporto");
                            out.flush();
                        }
                        */
                    } else {
                        response.setStatus(404);
                        out.println("Parametri mancanti");
                        out.flush();
                    }
                    break;
                case "logout":
                    HttpSession s = request.getSession(false);
                    s.invalidate();
                    out.println("Logout effettuato con successo");
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
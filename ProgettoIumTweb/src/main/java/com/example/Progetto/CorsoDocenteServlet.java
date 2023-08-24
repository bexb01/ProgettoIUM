package com.example.Progetto;

import DAO.Model;
import org.json.JSONObject;
import utils.JsonUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "CorsoDocenteServlet", value = "/CorsoDocenteServlet")
public class CorsoDocenteServlet extends HttpServlet {

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

        if(action.equals("lista docenti")) {
            model.getDocenti();
            //if
            out.println("Ecco la lisa docenti disponibili");
            out.flush();
            //else
        } else if(ruolo.equals("amministratore")) {
            switch (action) {
                case "insert docente":
                    if (!jsonObject.optString("nome").isEmpty() && !jsonObject.optString("cognome").isEmpty()) {
                        String nome = jsonObject.getString("nome");
                        String cognome = jsonObject.getString("cognome");
                        model.insertDocente(nome, cognome);
                        if (!jsonObject.optString("corso").isEmpty()) {
                            String corso = jsonObject.getString("corso");
                            //ciclo for che cicla sulla lista di corsi
                            //se non presenti inserisci i nuovi corsi nella tabella
                            model.insertCorso(corso);
                            //crea associazione corso_docente
                            model.insertCorsoDocente(nome, cognome, corso);
                            //fine ciclo
                        }
                        //AGGIUNGI IF
                        out.println("Docente inserito con successo");
                        out.flush();
                        //aggiungi ELSE
                        /*
                            response.setStatus(404);
                            out.println("Errore nella richiesta");
                            out.flush();
                        */
                    } else {
                        response.setStatus(404);
                        out.println("Parametri mancanti");
                        out.flush();
                    }
                    break;
                case "delete docente":
                    if (!jsonObject.optString("id_docente").isEmpty()) {
                        int id = jsonObject.getInt("id_docente");
                        model.deleteDocente(id);
                        //modifica paremetri metodi
                        //model.deleteCorsoDocente();
                        out.println("Docente eliminato con successo");
                        out.flush();
                    } else {
                        response.setStatus(404);
                        out.println("Errore nella selezione del docente");
                        out.flush();
                    }
                    break;
                case "delete corso":
                    //model.deleteCorso();
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

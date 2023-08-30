package com.example.Progetto;

import DAO.Corso;
import DAO.CorsoDocente;
import DAO.Docente;
import DAO.Model;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.JsonUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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
        JSONObject r = new JSONObject();

        if(action != null) {
            if (action.equals("lista docenti")) {
                ArrayList<Docente> listaDocenti = (ArrayList<Docente>) model.getListaDocenti();
                if (listaDocenti != null) {
                    JSONArray jsonArray = new JSONArray();
                    for (Docente docente : listaDocenti) {
                        JSONObject docenteJson = new JSONObject();
                        docenteJson.put("id_docente", docente.getId_docente());
                        docenteJson.put("nome", docente.getNome());
                        docenteJson.put("cognome", docente.getCognome());
                        jsonArray.put(docenteJson);
                    }
                    r.put("messaggio", "Lista docenti recuperata con successo");
                    r.put("lista docenti", jsonArray);
                } else {
                    response.setStatus(401);
                    r.put("messaggio", "Errore nella richiesta della lista docenti al server");
                }
            } else if (action.equals("lista corsi")) {
                ArrayList<Corso> listaCorsi = (ArrayList<Corso>) model.getListaCorsi();
                if (listaCorsi != null) {
                    JSONArray jsonArray = new JSONArray();
                    for (Corso corso : listaCorsi) {
                        JSONObject corsoJson = new JSONObject();
                        corsoJson.put("id_corso", corso.getId_corso());
                        corsoJson.put("nome", corso.getTitolo());
                        jsonArray.put(corsoJson);
                    }
                    r.put("messaggio", "Lista corsi recuperata con successo");
                    r.put("lista corsi", jsonArray);
                } else {
                    response.setStatus(401);
                    r.put("messaggio", "Errore nella richiesta della lista corsi al server");
                }
            }else if(action.equals("lista docenti corso")){
                System.out.println(jsonObject);
                if (jsonObject.has("id_corso")) {
                    ArrayList<CorsoDocente> listaCorsoDocente = (ArrayList<CorsoDocente>) model.getCorsoDocente(jsonObject.getInt("id_corso"));
                    if (listaCorsoDocente != null) {
                        JSONArray jsonArray = new JSONArray();
                        for (CorsoDocente corsodocente : listaCorsoDocente) {
                            JSONObject corsoDocentiJson = new JSONObject();
                            corsoDocentiJson.put("id_corso_docente", corsodocente.getId_corso());
                            corsoDocentiJson.put("id_docente", corsodocente.getId_docente());
                            corsoDocentiJson.put("id_corso", corsodocente.getId_corso());
                            corsoDocentiJson.put("nome", corsodocente.getNomeDocente());
                            corsoDocentiJson.put("cognome", corsodocente.getCognomeDocente());
                            corsoDocentiJson.put("titolo", corsodocente.getTitolo());
                            jsonArray.put(corsoDocentiJson);
                        }
                        r.put("messaggio", "Lista docenti per il corso recuperata con successo");
                        r.put("lista corsi docente", jsonArray);
                    }else {
                        response.setStatus(401);
                        r.put("messaggio", "Errore nella richiesta della lista docenti del corso al server");
                    }
                } else {
                    response.setStatus(404);
                    r.put("messaggio", "Parametri del corso mancanti");
                }
            } else if (ruolo != null && ruolo.equals("amministratore")) {
                switch (action) {
                    case "insert docente":
                        System.out.println(jsonObject);
                        if (!jsonObject.optString("nome").isEmpty() && !jsonObject.optString("cognome").isEmpty()) {
                            int idDocente = model.insertDocente(jsonObject.getString("nome"), jsonObject.getString("cognome"));
                            if (idDocente > 0) {
                                r.put("messaggio", "Docente inserito con successo");
                            } else {
                                response.setStatus(401);
                                r.put("messaggio", "Errore nell'inserimento del docente");
                            }
                        } else {
                            response.setStatus(404);
                            r.put("messaggio", "Parametri del docente mancanti");
                        }
                        break;
                    case "delete docente":
                        System.out.println(jsonObject);
                        if (jsonObject.has("id_docente")) {
                            int idDocente = model.deleteDocente(jsonObject.getInt("id_docente"));
                            if (idDocente > 0) {
                                r.put("messaggio", "Docente eliminato con successo");
                            } else {
                                response.setStatus(401);
                                r.put("messaggio", "Errore nell'eliminazione del docente");
                            }
                        } else {
                            response.setStatus(404);
                            r.put("messaggio", "Parametri del docente mancanti");
                        }
                        break;
                    case "insert corso":
                        System.out.println(jsonObject);
                        if (!jsonObject.optString("corso").isEmpty()) {
                            int idCorso = model.insertCorso(jsonObject.getString("corso"));
                            if (idCorso > 0) {
                                r.put("messaggio", "Corso inserito con successo");
                            } else {
                                response.setStatus(401);
                                r.put("messaggio", "Errore nell'inserimento del corso");
                            }
                        } else {
                            response.setStatus(404);
                            r.put("messaggio", "Parametri del corso mancanti");
                        }
                        break;
                    case "delete corso":
                        System.out.println(jsonObject);
                        if (!jsonObject.optString("corso").isEmpty()) {
                            int idCorso = model.deleteCorso(jsonObject.getString("corso"));
                            if (idCorso > 0) {
                                r.put("messaggio", "Corso rimosso con successo");
                            } else {
                                response.setStatus(401);
                                r.put("messaggio", "Errore nella rimozione del corso");
                            }
                        } else {
                            response.setStatus(404);
                            r.put("messaggio", "Parametri del corso mancanti");
                        }
                        break;
                    case "insert corso-docente":
                        System.out.println(jsonObject);
                        if (!jsonObject.optString("nome").isEmpty() && !jsonObject.optString("cognome").isEmpty() && !jsonObject.optString("corso").isEmpty()) {
                            int idCorsoDocente = model.insertCorsoDocente(jsonObject.getString("nome"), jsonObject.getString("cognome"), jsonObject.getString("corso"));
                            if (idCorsoDocente > 0) {
                                r.put("messaggio", "Associazione corso-docente inserito con successo");
                            } else {
                                response.setStatus(401);
                                r.put("messaggio", "Errore nell'inserimento del associazione corso-docente");
                            }
                        } else {
                            response.setStatus(404);
                            r.put("messaggio", "Parametri del corso o del docente mancanti");
                        }
                        break;
                    case "delete corso-docente":
                        System.out.println(jsonObject);
                        if (!jsonObject.optString("nome").isEmpty() && !jsonObject.optString("cognome").isEmpty() && !jsonObject.optString("corso").isEmpty()) {
                            int idCorsoDocente = model.deleteCorsoDocente(jsonObject.getString("nome"), jsonObject.getString("cognome"), jsonObject.getString("corso"));
                            if (idCorsoDocente > 0) {
                                r.put("messaggio", "Associazione corso-docente eliminata con successo");
                            } else {
                                response.setStatus(401);
                                r.put("messaggio", "Errore nell'eliminazione del associazione corso-docente");
                            }
                        } else {
                            response.setStatus(404);
                            r.put("messaggio", "Parametri del corso o del docente mancanti");
                        }
                        break;
                    default:
                        response.setStatus(404);
                        r.put("messaggio", "Errore nella richiesta");
                        break;
                }
            } else {
                response.setStatus(401);
                r.put("messaggio", "Non sei autorizzato [o parametro mancante]");
            }
        }else {
            response.setStatus(404);
            r.put("messaggio", "Parametro mancante");
        }
        out.println(r);
        out.flush();
    }

    @Override
    public void destroy() {
    }
}

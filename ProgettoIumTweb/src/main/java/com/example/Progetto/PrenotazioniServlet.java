package com.example.Progetto;

import DAO.Model;
import DAO.Prenotazione;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.JsonUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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
        JSONObject r = new JSONObject();

        if(ruolo != null && action != null){
            if(!ruolo.equals("guest")){
                if(ruolo.equals("cliente")) {
                    switch (action) {
                        case "prenota": //-->solo cliente
                            System.out.println(jsonObject);
                            if (jsonObject.has("id_utente") && jsonObject.has("id_corso_docente") && jsonObject.has("data") && jsonObject.has("ora")) {
                                int id_prenotazione = model.setPrenotazione(jsonObject.getInt("id_utente"), jsonObject.getInt("id_corso_docente"),  java.sql.Date.valueOf("data"),jsonObject.getInt("ora"));
                                if(id_prenotazione > 0){
                                    r.put("messaggio", "Prenotazione effettuata con successo");
                                }else{
                                    response.setStatus(401);
                                    r.put("messaggio", "Errore nella prenotazione della ripetizione");
                                }
                            }else {
                                response.setStatus(404);
                                r.put("messaggio", "Parametri della prenotazione mancanti");
                            }
                            break;
                        case "aggiorna prenotazione":   //-->solo cliente (setta come effettuata)
                            System.out.println(jsonObject);
                            if (jsonObject.has("id_prenotazione")){
                                int id_prenotazione = model.updatePrenotazione(jsonObject.getInt("id_prenotazione"));
                                if(id_prenotazione > 0){
                                    r.put("messaggio", "Ripetizione segnata come effettuata con successo");
                                }else{
                                    response.setStatus(401);
                                    r.put("messaggio", "Errore nell'aggiornamento della prenotazione");
                                }
                            }else {
                                response.setStatus(404);
                                r.put("messaggio", "Parametri della prenotazione mancanti");
                            }
                            break;
                        case "visualizza prenotazioni cliente"://-->solo cliente
                            System.out.println(jsonObject);
                            Object idUtenteObj = s.getAttribute("id_utente");
                            if (idUtenteObj instanceof Integer) {
                                ArrayList<Prenotazione> listaPrenotazioni = (ArrayList<Prenotazione>) model.getListaPrenotazioniUtente((int) s.getAttribute("id_utente"));
                                if (listaPrenotazioni != null) {
                                    JSONArray jsonArray = parseArrayToJson(listaPrenotazioni);
                                    r.put("messaggio", "Lista delle prenotazioni recuperata con successo");
                                    r.put("lista prenotazioni", jsonArray);
                                }else{
                                    response.setStatus(401);
                                    r.put("messaggio", "Errore nella richiesta della lista delle prenotazioni dell'utente al server");
                                }
                            }else {
                                response.setStatus(404);
                                r.put("messaggio", "Errore nella sessione utente, attributo id_utente non trovato");
                            }
                            break;
                        default:
                            response.setStatus(404);
                            r.put("messaggio", "Errore nella richiesta");
                            break;
                    }
                } else if(ruolo.equals("amministratore")){
                    if(action.equals("visualizza tutte le prenotazioni")){ //-->amministratore
                        //-->solo amministratore
                        System.out.println(jsonObject);
                        ArrayList<Prenotazione> listaPrenotazioni = (ArrayList<Prenotazione>) model.getListaPrenotazioni();
                        if (listaPrenotazioni != null) {
                            JSONArray jsonArray = parseArrayToJson(listaPrenotazioni);
                            r.put("messaggio", "Lista di tutte le prenotazioni recuperata con successo");
                            r.put("lista prenotazioni", jsonArray);
                        }else{
                            response.setStatus(401);
                            r.put("messaggio", "Errore nella richiesta della lista di tutte le prenotazioni al server");
                        }
                    }else {
                        response.setStatus(404);
                        r.put("messaggio", "Errore nella richiesta");
                    }
                }else if (action.equals("cancella prenotazione")) {   //-->cliente e amministratore
                    System.out.println(jsonObject);
                    if (jsonObject.has("id_prenotazione")) {
                        int idPrenotazione = model.deletePrenotazione(jsonObject.getInt("id_prenotazione"));
                        if(idPrenotazione > 0){
                            r.put("messaggio", "Prenotazione cancellata con successo");
                        }else{
                            response.setStatus(401);
                            r.put("messaggio", "Errore nella rimozione della prenotazione");
                        }
                    }else{
                        response.setStatus(404);
                        r.put("messaggio", "Parametri della prenotazione mancanti");
                    }
                }else if(action.equals("ripetizioni disponibili")) {    //--------------------------------------------------------------------------------
                    System.out.println(jsonObject);
                    //case: ripetizioni disponibili //-->cliente e amministratore
                    //restituisci lista prenotazioni del docente inviato e
                    Object idDocenteObj = s.getAttribute("id_docente");
                    if (idDocenteObj instanceof Integer) {
                        ArrayList<Prenotazione> listaPrenotazioniDocente = (ArrayList<Prenotazione>) model.getListaPrenotazioniDocente((int) s.getAttribute("id_docente"));
                        if (listaPrenotazioniDocente != null) {
                            JSONArray jsonArrayDocente = parseArrayToJson(listaPrenotazioniDocente);
                            r.put("lista prenotazioni docente", jsonArrayDocente);
                            //lista prenotazioni dell'utente
                            Object idUtenteObj = s.getAttribute("id_utente");
                            if (idUtenteObj instanceof Integer) {
                                ArrayList<Prenotazione> listaPrenotazioniUtente = (ArrayList<Prenotazione>) model.getListaPrenotazioniUtente((int) s.getAttribute("id_utente"));
                                if (listaPrenotazioniUtente != null) {
                                    JSONArray jsonArrayUtente = parseArrayToJson(listaPrenotazioniUtente);
                                    r.put("messaggio", "Lista delle prenotazioni recuperata con successo");
                                    r.put("lista prenotazioni utente", jsonArrayUtente);
                                }else{
                                    response.setStatus(401);
                                    r.put("messaggio", "Errore nella richiesta al server");
                                }
                            }else {
                                response.setStatus(404);
                                r.put("messaggio", "Errore nella sessione utente, attributo id_utente non trovato");
                            }
                        }else{
                            response.setStatus(401);
                            r.put("messaggio", "Errore nella richiesta della lista delle prenotazioni dell'utente al server");
                        }
                    }
                    //frontend renderle non cliccabili-----------------------------------------------------------------------------------------------------------------------
                } else {
                    response.setStatus(401);
                    r.put("messaggio", "Non sei autorizzato");
                }
            }else {
                response.setStatus(401);
                r.put("messaggio", "Errore nel ruolo");
            }
        } else {
            response.setStatus(401);
            r.put("messaggio", "Dati mancanti");
        }
        out.println(r);
        out.flush();
    }

    private JSONArray parseArrayToJson(ArrayList<Prenotazione> listaPrenotazioni) {
        JSONArray jsonArray = new JSONArray();
        for (Prenotazione prenotazione : listaPrenotazioni) {
            JSONObject prenotazioneJson = new JSONObject();
            prenotazioneJson.put("id_prenotazione", prenotazione.getId_prenotazione());
            prenotazioneJson.put("id_utente", prenotazione.getId_utente());
            prenotazioneJson.put("id_corso_docente", prenotazione.getId_corso_docente());
            prenotazioneJson.put("data", prenotazione.getData());
            prenotazioneJson.put("ora", prenotazione.getOra());
            jsonArray.put(prenotazioneJson);
        }
        return jsonArray;
    }

    @Override
    public void destroy() {
    }
}

package com.example.Progetto;

import DAO.Model;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*-----------------------PROVA (DA MODIFICARE)-----------------------*/
@WebServlet(name = "ControllerServlet", value = "/controller-servlet")
public class ControllerServlet extends HttpServlet {

    private Model model = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // Recupera il model del DAO dal ServletContext
        ServletContext ctx = getServletContext();
        this.model = (Model) ctx.getAttribute("DAO");

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Gestisci richieste GET
        String action = request.getParameter("action");
        if (action != null) {
            switch (action) {
                case "viewCatalog":
                    // Carica il catalogo delle ripetizioni disponibili e passa alla vista
                    //model.getSlotLiberi();
                    // ...
                    break;
                case "viewUserBookings":
                    // Carica le prenotazioni dell'utente corrente e passa alla vista
                    model.getPrenotazioniUtente(1); //USARE GetAttribute() o simili
                    // ...
                    break;
                // Altre azioni
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Gestisci richieste POST
        String action = request.getParameter("action");
        if (action != null) {
            switch (action) {
                case "bookLesson":
                    // Esegue la prenotazione della ripetizione
                    model.setPrenotazione(1,1,1,"10 maggio",10);    //USARE getAttribute() o simili
                    // ...
                    break;
                case "cancelBooking":
                    // Esegue la cancellazione di una prenotazione
                    model.deletePrenotazione(1);    //USARE getAttribute() o simili
                    // ...
                    break;
                case "lezione eseguita":
                    model.updatePrenotazione(1);    //PRENDI DATI DA VIEW
                    break;
                // Altre azioni
                case "inserisci corso":
                    //prendi dati inseriti da docente se il corso non esiste ne creo uno nuovo??
                    model.insertCorso("Filosofia"); //???
                    //insersci associazione nella tabella corso_docente
                    break;
                case "inserisci docente":
                    model.insertUtente("Federico","Lucia", "fedez@gmail.com", "ihatecodacons");  //PRENDI DATI DA FORM E PASSALI QUI
                    model.insertDocente("Federico","Lucia"); //PRENDI DATI DA FORM E PASSALI QUI
                    //insertDocenteCorso()  //PRENDI DATI DA FORM E PASSALI QUI
                    break;
                case "inserisci utente":
                    model.insertUtente("Marco","Carta", "marcocarta@gmail.com", "ihatemyself"); //PRENDI DATI DA FORM E PASSALI QUI
                    break;
                case "delete corso":
                    model.deleteCorso("Programmazione"); //PRENDI DATI DA FORM E PASSALI QUI
                    break;
                case "delete docente":
                    //model.deleteDocente("Marco", "Carta"); //PRENDI DATI DA FORM E PASSALI QUI
                    break;
                case "delete utente":
                    model.deleteUtente(1);
                    break;

            }
        }
    }

    @Override
    public void destroy() {
    }
}

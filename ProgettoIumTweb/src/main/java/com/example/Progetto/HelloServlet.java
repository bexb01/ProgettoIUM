package com.example.Progetto;

import DAO.*;

import java.io.*;
import java.util.Date;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

/*-----------------------TESTING CLASS-----------------------*/
@WebServlet(name = "HelloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private Model model = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext ctx= getServletContext();
        String url = ctx.getInitParameter("DB-URL");
        String user = ctx.getInitParameter("user");
        String pwd = ctx.getInitParameter("pwd");
        System.out.println("URL del database: " + url);
        model = new Model(url, user, pwd);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        /*
        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Informazioni</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("Hai chiesto la lista delle prenotazioni: ");
            List<Prenotazione> prenotazioni = model.getPrenotazioni();
            for (Prenotazione prenotazione : prenotazioni) {
                out.println("<p>" + prenotazione + "</p>");
            }
            out.println("</body>");
            out.println("</html>");
        }
         */
        response.setContentType("text/html");
        model.insertUtente( "Beatrice", "Matera", "materabeatrice@unito.it", "abracadabra");     //amministratore
        model.insertUtente( "Arlo", "Merlo", "acab@unito.it", "brrrriz");    //utente
        try (PrintWriter out = response.getWriter()) {
            out.println("<html><body>");
            out.println("<h1> Risultato </h1>");
            out.println("<p> Upload utenti avvenuto con successo 1</p> ");
            out.println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException  {
        //CONTROLLARE CONSISTENZA DI QUELLO CHE ARRIVA
        //DATI UTENTE: con vue mandati oggetti json che sono spacchettati lato server
        //controlli per assicurarsi che le azioni siano eseguite in maniera adatta
        /*
        response.setContentType("text/html");
        model.insertCorso("Letteratura inglese");
        try (PrintWriter out = response.getWriter()) {
            out.println("<html><body>");
            out.println("<h1> Risultato </h1>");
            out.println("<p> Upload avvenuto con successo </p> ");
            out.println("</body></html>");
        }
         */
        processRequest(request,response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String userName = request.getParameter("login");
        HttpSession s = request.getSession();
        if (userName!=null)     //prima volta che si accede quindi ottiene lo username e lo setto (le altre volte sarà null perchè accessi successivi, mi dovrei ricordare di lui)
            s.setAttribute("userName", userName);
        String url = response.encodeURL("life1-servlet");
        PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Life1</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<p>Sei collegato come: " + s.getAttribute("userName") + "</p>");
            String azione = request.getParameter("action");
            out.println("<p>URL: " + url + "</p>");
            if (azione!=null && azione.equals("invalida")) {
                s.invalidate();
                out.println("<p>Sessione invalidata!</p>");
                out.println("<p>Ricarica <a href=\"" + url + "\"> la pagina</a></p>");
            }
            else {
                out.print("<p>Stato della sessione: ");
                if (s.isNew())
                    out.println(" nuova sessione</p>");
                else out.println(" vecchia sessione</p>");
                out.println("<p>ID di sessione: "+s.getId() + "</p>");
                out.println("<p>Data di creazione: " + new Date(s.getCreationTime()) + "</p>");
                out.println("<p>Max inactive time interval (in secondi): "
                        + s.getMaxInactiveInterval() + "</p>");
                out.println("<p>Invalida <a href=\"" + url + "?action=invalida\"> la sessione</a></p>");
                out.println("<p>Ricarica <a href=\"" + url + "\"> la pagina</a></p>");
            }
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    public void destroy() {
    }
}
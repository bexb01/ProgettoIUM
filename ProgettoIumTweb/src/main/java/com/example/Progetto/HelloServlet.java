package com.example.Progetto;

import DAO.*;

import java.io.*;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "HelloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private Model model = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext ctx= getServletContext();
        String url = ctx.getInitParameter("DB-URL");
        String user = ctx.getInitParameter("user");
        String pwd = ctx.getInitParameter("pwd");
        // Notare la stampa nel log del Server ??
        System.out.println("URL del database: " + url);
        model = new Model(url, user, pwd);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        model.insertCorso("Informatica");
        try (PrintWriter out = response.getWriter()) {
            out.println("<html><body>");
            out.println("<h1> Risultato </h1>");
            out.println("<p> Upload avvenuto con successo 1</p> ");
            out.println("</body></html>");
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String n = request.getParameter("nome");    //deploy senza nient altro può essere invocata da chiunque
        if(n!=null){    //nome e cognome diverso da null può essere inserito nel db usando il DAO

        }
        response.setContentType("text/html");
        model.insertCorso("Letteratura inglese");
        try (PrintWriter out = response.getWriter()) {
            out.println("<html><body>");
            out.println("<h1> Risultato </h1>");
            out.println("<p> Upload avvenuto con successo </p> ");
            out.println("</body></html>");
        }
    }

    public void destroy() {
    }
}
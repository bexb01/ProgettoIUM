package com.example.Progetto;

import DAO.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PrenotazioniServlet extends HttpServlet {
    private Model model = null;

    @Override
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Catalogo Ripetizioni</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Catalogo Ripetizioni</h1>");

        List<Corso> slotLiberi = model.getSlotLiberi();

        if (slotLiberi != null && !slotLiberi.isEmpty()) {
            out.println("<ul>");
            for (Corso slot : slotLiberi) {
                out.println("<li>");
                /*
                out.println("Corso: " + slot.getCorso() + ", Docente: " + slot.getDocente() +
                        ", Giorno: " + slot.getGiorno() + ", Orario: " + slot.getOrario());
                out.println("</li>");

                 */
            }
            out.println("</ul>");
        } else {
            out.println("<p>Nessuna ripetizione disponibile.</p>");
        }

        out.println("</body>");
        out.println("</html>");
    }
}


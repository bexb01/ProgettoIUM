package DAO;

import java.util.Date;

public class Prenotazione {
    private int id_prenotazione;
    private int id_utente;
    private int id_corso_docente;
    private String utente;
    private String docente;
    private String materia;
    private Date data;
    private int ora;
    private String stato;


    public Prenotazione(int id_prenotazione, int id_utente, int id_corso_docente, Date data, int ora, String stato) {
        this.id_prenotazione = id_prenotazione;
        this.id_utente = id_utente;
        this.id_corso_docente = id_corso_docente;
        this.data = data;
        this.ora = ora;
        this.stato = stato;
    }

    public Prenotazione(int id_prenotazione, int id_utente, int id_corso_docente,String utente, String docente, String materia, Date data, int ora, String stato) {
        this.id_prenotazione = id_prenotazione;
        this.id_utente = id_utente;
        this.id_corso_docente = id_corso_docente;
        this.utente= utente;
        this.docente= docente;
        this.materia = materia;
        this.data = data;
        this.ora = ora;
        this.stato = stato;
    }

    public int getId_prenotazione() {
        return id_prenotazione;
    }

    public int getId_utente() {
        return id_utente;
    }

    public int getId_corso_docente() {
        return id_corso_docente;
    }

    public Date getData() {
        return data;
    }

    public int getOra() {
        return ora;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getUtente() {
        return utente;
    }

    public String getDocente() {
        return docente;
    }

    public String getMateria() {
        return materia;
    }

    @Override
    public String toString() {
        return "Prenotazione{" +
                "id_prenotazione=" + id_prenotazione +
                ", id_utente=" + id_utente +
                ", id_corso=" + id_corso_docente +
                ", data='" + data + '\'' +
                ", ora=" + ora +
                ", stato='" + stato + '\'' +
                '}';
    }
}

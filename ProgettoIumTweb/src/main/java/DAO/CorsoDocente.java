package DAO;

public class CorsoDocente {
    private int id_corso_docente;
    private int id_docente;
    private int id_corso;
    private String nomeDocente;
    private String cognomeDocente;
    private String titoloCorso;


    public CorsoDocente(int id_corso_docente, int id_docente, int id_corso) {
        this.id_corso_docente = id_corso_docente;
        this.id_docente = id_docente;
        this.id_corso = id_corso;
    }

    public CorsoDocente(int id_corso_docente, int id_docente, int id_corso, String nomeDocente, String cognomeDocente, String titoloCorso) {
        this.id_corso_docente = id_corso_docente;
        this.id_docente = id_docente;
        this.id_corso = id_corso;
        this.nomeDocente = nomeDocente;
        this.cognomeDocente = cognomeDocente;
        this.titoloCorso = titoloCorso;
    }

    public int getId_corso_docente() {
        return id_corso_docente;
    }

    public int getId_docente() {
        return id_docente;
    }

    public int getId_corso() {
        return id_corso;
    }

    @Override
    public String toString() {
        return "CorsoDocente{" +
                "id_corso_docente=" + id_corso_docente +
                ", id_docente=" + id_docente +
                ", id_corso=" + id_corso +
                '}';
    }
}

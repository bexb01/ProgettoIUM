package DAO;

public class Docente {
    private int id_docente;
    private String nome;
    private String cognome;
    private boolean attivo;


    public Docente(int id_docente, String nome, String cognome) {
        this.id_docente = id_docente;
        this.nome = nome;
        this.cognome = cognome;
        this.attivo = true;
    }

    public int getId_docente() {
        return id_docente;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public boolean getAttivo() { return attivo; }
    public void setAttivo(boolean value) { this.attivo = value; }

    @Override
    public String toString() {
        return id_docente + " " + nome + " " + cognome;
    }
}

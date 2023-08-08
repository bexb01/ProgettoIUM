package DAO;

public class Corso {
    private int id_corso;
    private String titolo;
    private boolean attivo;

    public Corso(int id_corso, String titolo) {
        this.id_corso = id_corso;
        this.titolo = titolo;
        this.attivo = true;
    }

    public int getId_corso() {
        return id_corso;
    }

    public String getTitolo() {
        return titolo;
    }

    public boolean getAttivo() { return attivo; }
    public void setAttivo(boolean value) { this.attivo = value; }

    @Override
    public String toString() {
        return id_corso + " " + titolo;
    }
}

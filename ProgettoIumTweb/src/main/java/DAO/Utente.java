package DAO;

public class Utente {
    private int id_utente;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private boolean amministratore;
    private boolean attivo;


    public Utente(String nome, String cognome, String email, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.amministratore = false;
        this.attivo = true;
    }

    public int getId_utente() {
        return id_utente;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean getAmministratore() {
        return amministratore;
    }

    public String getRuolo(){
        if (getAmministratore())
            return "amministratore";
        else
            return "utente";
    }

    public void setAmministratore() {
        this.amministratore = amministratore;
    }

    public boolean isAttivo() {
        return attivo;
    }

    public void setAttivo(boolean attivo) {
        this.attivo = attivo;
    }

    @Override
    public String toString() {
        return "Utente{" +
                "id_utente=" + id_utente +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", ruolo='" + amministratore + '\'' +
                ", attivo=" + attivo +
                '}';
    }
}

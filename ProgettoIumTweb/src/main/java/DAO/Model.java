package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Model {

    //Valori inseriti in web.xml
    private String url1 = "";
    private String user = "";
    private String password = "";

    public Model(String url, String user, String pwd) {
        this.url1 = url;
        this.user = user;
        this.password = pwd;
        registerDriver();
    }

    private static void registerDriver() {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            System.out.println("Driver correttamente registrato");
        } catch (SQLException e) {
            System.out.println("Errore: Driver non trovato" + e.getMessage());
        }
    }

    public void insertCorso(String corso) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "INSERT INTO CORSO (titolo) VALUES (?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, corso);
                ps.executeUpdate();
                System.out.println("Inserito corso " + corso + " con successo");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'inserimento del corso: " + e.getMessage());
        }
    }

    public List<Corso> getCorsi() {
        List<Corso> corsi = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT * FROM CORSO WHERE attivo = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, true);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Corso corso = new Corso(rs.getInt("ID_CORSO"), rs.getString("TITOLO"));
                    corsi.add(corso);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return corsi;
    }

    public void deleteCorso(int id) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "UPDATE CORSO SET attivo = ? WHERE id_corso = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, false);
                ps.setInt(2, id);
                ps.executeUpdate();
                System.out.println("Corso con ID " + id + " disattivato con successo.");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la disattivazione del corso: " + e.getMessage());
        }
    }

    //da usare quando viene creato account docente
    public void insertDocente(String nome, String cognome) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "INSERT INTO DOCENTE (nome, cognome) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, nome);
                ps.setString(2, cognome);
                ps.executeUpdate();
                System.out.println("Inserito docente " + nome + " " + cognome + " con successo");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'inserimento del docente: " + e.getMessage());
        }
    }

    public List<Docente> getDocenti() {
        List<Docente> docenti = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT * FROM DOCENTE WHERE attivo = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, true);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Docente docente = new Docente(rs.getInt("ID_DOCENTE"), rs.getString("NOME"),rs.getString("COGNOME"));
                    docenti.add(docente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return docenti;
    }

    //getDocentiDisponibili ??(not in prenotazioni [prenotazioni where fascia oraria selezionata])??

    public void deleteDocente(int id) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "UPDATE DOCENTE SET attivo = ? WHERE id_docente = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, false);
                ps.setInt(2, id);
                ps.executeUpdate();
                System.out.println("Docente con ID " + id + " disattivato con successo.");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la disattivazione del docente: " + e.getMessage());
        }
    }

    //da usare quando viene creato un profilo utente da uno studente
    public void insertUtente(String nome, String cognome, String email, String pswd, boolean amministratore) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "INSERT INTO UTENTE (nome, cognome, email, password, amministratore) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, nome);
                ps.setString(2, cognome);
                ps.setString(3, email);
                ps.setString(4, pswd);
                ps.setBoolean(5, amministratore);
                ps.executeUpdate();
                System.out.println("Inserito utente " + nome + " " + cognome + " con amministratore " + amministratore + " con successo");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'inserimento dell'utente: " + e.getMessage());
        }
    }

    public String checkUtente(String email, String pswd) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT amministratore FROM UTENTE WHERE attivo = ? AND email = ? AND password= ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, true);
                ps.setString(2, email);
                ps.setString(3, pswd);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                         boolean ruolo = rs.getBoolean("AMMINISTRATORE"); // true se amministratore
                        if(ruolo)
                            return "amministratore";
                        else
                            return "utente";
                    } else {
                        return "sconosciuto";
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'interazione con il database (checkutente): " + e.getMessage());
        }
        return "ritenta";   // connection failed
    }

    public void deleteUtente(int id) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "UPDATE UTENTE SET attivo = ? WHERE id_utente = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, false);
                ps.setInt(2, id);
                ps.executeUpdate();
                System.out.println("Utente con ID " + id + " disattivato con successo.");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la disattivazione del utente: " + e.getMessage());
        }
    }


    //come tipo dovrebbe andare CorsoDocente
    public List<Corso> getSlotLiberi(){
        List<Corso> slotLiberi = new ArrayList<>();
        return slotLiberi;
    }
    //Forse conviene usare prepared statement
    public List<Prenotazione> getPrenotazioni() {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT * FROM PRENOTAZIONE";
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { //forse cambiare con controllo hasnext()
                    Prenotazione p = new Prenotazione(rs.getInt("ID_PRENOTAZIONE"),
                            rs.getInt("ID_UTENTE"),
                            rs.getInt("ID_CORSO"),
                            rs.getInt("ID_DOCENTE"),
                            rs.getString("DATA"),
                            rs.getInt("ORA"),
                            rs.getString("STATO"));
                    prenotazioni.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il recupero delle prenotazioni: " + e.getMessage());
        }
        return prenotazioni;
    }

    //VARIANTE VEDI QUALE TRA QUESTA E QUELLA DI SOPRA CONVIENE AVERE
    public List<Prenotazione> getPrenotazioniUtente(int idUtente) {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT * FROM Prenotazione WHERE id_utente = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, idUtente);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Prenotazione prenotazione = new Prenotazione(rs.getInt("ID_PRENOTAZIONE"),
                            rs.getInt("ID_UTENTE"),
                            rs.getInt("ID_CORSO"),
                            rs.getInt("ID_DOCENTE"),
                            rs.getString("DATA"),
                            rs.getInt("ORA"),
                            rs.getString("STATO"));
                    prenotazioni.add(prenotazione);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prenotazioni;
    }

    public void setPrenotazione(int idUtente, int idCorso, int idDocente, String data, int ora) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "INSERT INTO Prenotazione (id_utente, id_corso, id_docente, data, ora, stato) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, idUtente);
                ps.setInt(2, idCorso);
                ps.setInt(3, idDocente);
                ps.setString(4, data);
                ps.setInt(3, ora);
                ps.setString(3, "attiva");
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePrenotazione(int idPrenotazione) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "UPDATE CORSO SET stato = ? WHERE id_prenotazione = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, "disdetta");
                ps.setInt(2, idPrenotazione);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePrenotazione(int idPrenotazione) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "UPDATE CORSO SET stato = ? WHERE id_prenotazione = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, "effettuata");
                ps.setInt(2, idPrenotazione);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

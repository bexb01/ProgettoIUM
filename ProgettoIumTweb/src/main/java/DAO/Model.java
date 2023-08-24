package DAO;

import org.mindrot.jbcrypt.BCrypt;

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

    public synchronized int insertCorso(String corso) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "INSERT INTO CORSO (titolo) VALUES (?)";
            try (PreparedStatement ps = conn.prepareStatement(query,Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, corso);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        return (int) rs.getLong(1);
                    } else {
                        return 0;
                    }
                }
                System.out.println("Inserito corso " + corso + " con successo");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'inserimento del corso: " + e.getMessage());
        }
        return 0;
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
                System.out.println("Corsi aggiunti con successo.");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il recupero della lista corsi: " + e.getMessage());
        }
        return corsi;
    }

    public synchronized void deleteCorso(String titolo) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "UPDATE CORSO SET attivo = ? WHERE id_corso = (SELECT id_corso FROM CORSO WHERE titolo = ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, false);
                ps.setString(2, titolo);
                ps.executeUpdate();
                System.out.println("Corso " + titolo + " disattivato con successo.");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la disattivazione del corso: " + e.getMessage());
        }
    }

    //da usare quando viene creato account docente
    public synchronized int insertDocente(String nome, String cognome) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "INSERT INTO DOCENTE (nome, cognome) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nome);
                ps.setString(2, cognome);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        return (int) rs.getLong(1);
                    } else {
                        return 0;
                    }
                }
                System.out.println("Inserito docente " + nome + " " + cognome + " con successo");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'inserimento del docente: " + e.getMessage());
        }
        return 0;
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

    public synchronized String deleteDocente(int id) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "UPDATE DOCENTE SET attivo = ? WHERE id_docente = ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, false);
                ps.setInt(1, id);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        return "Docente disattivato con successo.";
                    } else {
                        return "Errore durante la disattivazione del docente.";
                    }
                }

                System.out.println("Docente "+ id + " disattivato con successo.");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la disattivazione del docente: " + e.getMessage());
        }
        return "Errore nella connessione con il server";
    }

    public synchronized int insertCorsoDocente(String nomeDocente, String cognomeDocente, String corso){
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "INSERT INTO CORSO_DOCENTE (id_docente, id_corso) " +
                    "VALUES ((SELECT id_docente FROM DOCENTE WHERE nome = ? AND cognome = ?), " +
                    "(SELECT id_corso FROM CORSO WHERE titolo = ?))";
            try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nomeDocente);
                ps.setString(2, cognomeDocente);
                ps.setString(3, corso);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    System.out.println("Inserita associazione docente " + nomeDocente + " " + cognomeDocente + " e corso " + corso + " con successo");
                    return (int) rs.getLong(1);
                } else {
                    System.out.println("Associazione non creata, docente o corso non trovato.");
                    return 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'inserimento dell'associazione: " + e.getMessage());
        }
        return 0;
    }

    //DA CORREGGERE--------------------------------------------------------------------------------------------
    //restituisci coppia di array <Corso,Docente>
    //corsi attivi da controllare????
    public List getCorsoDocente(){
        List<CorsoDocente> corsiDocenti = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT cd.id_corso_docente, d.id_docente, c.id_corso, d.nome, d.cognome, c.titolo " +
                    "FROM Docente d " +
                    "JOIN Corso_Docente cd ON d.id_docente = cd.id_docente " +
                    "JOIN Corso c ON cd.id_corso = c.id_corso " +
                    "WHERE d.attivo = ?";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, true);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int idCorsoDocente = rs.getInt("id_corso_docente");
                        int idDocente = rs.getInt("id_docente");
                        int idCorso = rs.getInt("id_corso");
                        String nomeDocente = rs.getString("nome");
                        String cognomeDocente = rs.getString("cognome");
                        String titoloCorso = rs.getString("titolo");

                        CorsoDocente corsoDocente = new CorsoDocente(idCorsoDocente, idDocente, idCorso, nomeDocente, cognomeDocente, titoloCorso);
                        corsiDocenti.add(corsoDocente);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return corsiDocenti;
    }
    //-------------------------------------------------------------------------------------------------------------------

    public synchronized void deleteCorsoDocente(String nomeDocente, String cognomeDocente, String corso){
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "DELETE FROM CORSO_DOCENTE WHERE id_docente = (SELECT id_docente FROM DOCENTE WHERE nome = ? AND cognome = ?) " +
                    "AND id_corso = (SELECT id_corso FROM CORSO WHERE titolo = ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, nomeDocente);
                ps.setString(2, cognomeDocente);
                ps.setString(3, corso);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Rimossa associazione docente " + nomeDocente + " " + cognomeDocente + " e corso " + corso + " con successo");
                } else {
                    System.out.println("Associazione non trovata, nessuna cancellazione effettuata.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la rimozione dell'associazione: " + e.getMessage());
        }
    }

    //da usare quando viene creato un profilo utente da uno studente
    public synchronized int insertUtente(String nome, String cognome, String email, String pswd) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "INSERT INTO UTENTE (nome, cognome, email, password, amministratore) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nome);
                ps.setString(2, cognome);
                ps.setString(3, email);
                ps.setString(4,  BCrypt.hashpw(pswd, BCrypt.gensalt()));
                ps.setBoolean(5, false);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        return (int) rs.getLong(1);
                    } else {
                        return 0;
                    }
                }
                System.out.println("Inserito utente " + nome + " " + cognome + " con successo");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'inserimento dell'utente: " + e.getMessage());
        }
        return 0;
    }

    public Utente getUtente(String email, String pswd) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT * FROM UTENTE WHERE attivo = true AND email = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String passw1= rs.getString("password");
                        if(BCrypt.checkpw(pswd,passw1)){
                            Utente u = new Utente(rs.getString("nome"),
                                    rs.getString("cognome"),
                                    rs.getString("email"),
                                    rs.getString("password"));
                            return u;
                        }else{
                            return null;    // Password non corrisponde
                        }
                    } else {
                        return null;    // Utente non trovato
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'interazione con il database (getUtente): " + e.getMessage());
        }
        return null;    // Errore generico
    }

    public boolean validateEmail(String email){
        //in sign up vedere se qualcuno ha gia usato stessa mail
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT * FROM UTENTE WHERE attivo = ? AND email = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, true);
                ps.setString(2, email);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0)  //email already in use---Error
                    return false;
                 else
                     return true;
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'interazione con il database (getUtente): " + e.getMessage());
        }
        return false;
    }

    public synchronized void deleteUtente(int id) {
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


    //oppure passi lista di prenotazioni effettuate e cancelli front end quelle che ti vengono passate
    public List<Integer> getAvailableSlotsForActiveDocenti() {
        List<Integer> availableSlots = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT DISTINCT p.data, p.ora " +
                    "FROM corso_docente cd " +
                    "JOIN docente d ON cd.id_docente = d.id_docente " +
                    "JOIN corso c ON cd.id_corso = c.id_corso " +
                    "LEFT JOIN prenotazioni p ON cd.id_corso_docente = p.id_corso_docente " +
                    "WHERE d.attivo = ? AND p.id_prenotazione IS NULL";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, true);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int ora = rs.getInt("ora");
                        availableSlots.add(ora);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableSlots;
    }

    public List<Prenotazione> getPrenotazioni() {
        List<Prenotazione> prenotazioni = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT * FROM PRENOTAZIONE";
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { //forse cambiare con controllo hasnext()
                    Prenotazione p = new Prenotazione(rs.getInt("id_prenotazione"),
                            rs.getInt("id_utente"),
                            rs.getInt("id_corso_docente"),
                            rs.getString("data"),
                            rs.getInt("ora"),
                            rs.getString("stato"));
                    prenotazioni.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il recupero delle prenotazioni: " + e.getMessage());
        }
        return prenotazioni;
    }

    public List<Prenotazione> getPrenotazioniUtente(int idUtente) {     //controllare stato utente oppure no?
        List<Prenotazione> prenotazioni = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT * FROM Prenotazione WHERE id_utente = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, idUtente);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Prenotazione prenotazione = new Prenotazione(rs.getInt("id_prenotazione"),
                            rs.getInt("id_utente"),
                            rs.getInt("id_corso_docente"),
                            rs.getString("data"),
                            rs.getInt("ora"),
                            rs.getString("stato"));
                    prenotazioni.add(prenotazione);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prenotazioni;
    }

    public List<String> getPrenotazioniDocente(){
        List<String> prenotazioni = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT DISTINCT c.ora " +
                    "FROM corso_docente cd " +
                    "JOIN docente d ON cd.id_docente = d.id_docente " +
                    "JOIN corso c ON cd.id_corso = c.id_corso " +
                    "LEFT JOIN prenotazioni p ON cd.id_corso_docente = p.id_corso_docente " +
                    "WHERE d.attivo = ?";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, true);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int ora = rs.getInt("ora");
                        //prenotazioni.add(ora);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return prenotazioni;
    }

    //check docente attivo
    public synchronized int setPrenotazione(int idUtente, int idCorso, int idDocente, String data, int ora) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "INSERT INTO Prenotazione (id_utente, id_corso, id_docente, data, ora, stato) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, idUtente);
                ps.setInt(2, idCorso);
                ps.setInt(3, idDocente);
                ps.setString(4, data);
                ps.setInt(3, ora);
                ps.setString(3, "attiva");
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        return (int) rs.getLong(1);
                    } else {
                        return 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public synchronized void deletePrenotazione(int idPrenotazione) {
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

    public synchronized void updatePrenotazione(int idPrenotazione) {
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

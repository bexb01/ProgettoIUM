package DAO;

import org.mindrot.jbcrypt.BCrypt;
import utils.UserValidationResult;

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
                        System.out.println("Inserito corso " + corso + " con successo");
                        return (int) rs.getLong(1);
                    } else {
                        System.out.println("Errore durante la query per l'inserimento del corso:");
                        return 0;
                    }
                }
            }
            return 0;
        } catch (SQLException e) {
            System.out.println("Errore durante l'inserimento del corso: " + e.getMessage());
            return 0;
        }
    }

    public List<Corso> getListaCorsi() {
        List<Corso> corsi = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT * FROM CORSO WHERE attivo = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, true);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Corso corso = new Corso(rs.getInt("id_corso"), rs.getString("titolo"));
                    corsi.add(corso);
                }
                System.out.println("Corsi aggiunti con successo.");
                return corsi;
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il recupero della lista corsi: " + e.getMessage());
            return null;
        }
    }

    public synchronized int deleteCorso(String titolo) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "UPDATE CORSO SET attivo = ? WHERE id_corso = (SELECT id_corso FROM CORSO WHERE titolo = ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, false);
                ps.setString(2, titolo);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("Corso " + titolo + " disattivato con successo.");
                    return 1;
                }else{
                    System.out.println("Errore durante la query per la disattivazione del corso:");
                    return 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la disattivazione del corso: " + e.getMessage());
            return 0;
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
                        System.out.println("Inserito docente " + nome + " " + cognome + " con successo");
                        return (int) rs.getLong(1);
                    } else {
                        System.out.println("Errore durante la query per l'inserimento del docente:");
                        return 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'inserimento del docente: " + e.getMessage());
            return 0;
        }
        return 0;
    }

    public List<Docente> getListaDocenti() {
        List<Docente> docenti = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT * FROM DOCENTE WHERE attivo = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, true);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Docente docente = new Docente(rs.getInt("id_docente"),
                            rs.getString("nome"),
                            rs.getString("cognome"));
                    docenti.add(docente);
                }
                System.out.println("Lista docenti restituita con successo.");
                return docenti;
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la restituzione della lista docenti: " + e.getMessage());
            return null;
        }
    }


    public synchronized int deleteDocente(int id) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "UPDATE DOCENTE SET attivo = ? WHERE id_docente = ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, false);
                ps.setInt(1, id);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        System.out.println("Docente "+ id + " disattivato con successo.");
                        return id;
                    } else {
                        System.out.println("Errore durante la query per la disattivazione del docente.");
                        return 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la disattivazione del docente: " + e.getMessage());
            return 0;
        }
        return 0;
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
            return 0;
        }
    }

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
                    System.out.println("Lista corsi-docenti restituita con successo.");
                    return corsiDocenti;
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la restituzione della lista corsi-docenti: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public synchronized int deleteCorsoDocente(String nomeDocente, String cognomeDocente, String corso){
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
                    return 1;
                } else {
                    System.out.println("Associazione corso-docente non trovata, nessuna cancellazione effettuata.");
                    return 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la rimozione dell'associazione corso-docente: " + e.getMessage());
            return 0;
        }
    }

    public UserValidationResult validateEmail(String email) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT COUNT(*) FROM UTENTE WHERE attivo = ? AND email = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, true);
                ps.setString(2, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        if (count == 0) {
                            return new UserValidationResult(null, null); // No error
                        } else {
                            return new UserValidationResult(null, "L'indirizzo email è già in uso");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            return new UserValidationResult(null, "Errore durante la validazione dell'email: " + e.getMessage());
        }
        return new UserValidationResult(null, "Errore sconosciuto durante la validazione dell'email");
    }

    public synchronized UserValidationResult insertUtente(String nome, String cognome, String email, String pswd) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            UserValidationResult validationResult = validateEmail(email);
            if (validationResult.getErrorMessage() != null ) {
                return new UserValidationResult(null, validationResult.getErrorMessage());
            }
            String query = "INSERT INTO UTENTE (nome, cognome, email, password, attivo) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nome);
                ps.setString(2, cognome);
                ps.setString(3, email);
                ps.setString(4, BCrypt.hashpw(pswd, BCrypt.gensalt()));
                ps.setBoolean(5, false);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        int userId = rs.getInt(1);
                        Utente newUser = new Utente(userId, nome, cognome, email, pswd);
                        return new UserValidationResult(newUser, null); // No error
                    } else {
                        return new UserValidationResult(null, "Errore durante la query per l'inserimento dell'utente");
                    }
                }
            }
        } catch (SQLException e) {
            return new UserValidationResult(null, "Errore durante l'inserimento dell'utente: " + e.getMessage());
        }
        return new UserValidationResult(null, "Errore sconosciuto durante l'inserimento dell'utente");
    }

    public UserValidationResult getUtente(String email, String pswd) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT * FROM UTENTE WHERE attivo = true AND email = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String passw1 = rs.getString("password");
                        if (BCrypt.checkpw(pswd, passw1)) {
                            Utente u = new Utente(rs.getInt("id_utente"),
                                    rs.getString("nome"),
                                    rs.getString("cognome"),
                                    rs.getString("email"),
                                    rs.getString("password"));
                            return new UserValidationResult(u, null); // No error
                        } else {
                            return new UserValidationResult(null, "Password errata");
                        }
                    } else {
                        return new UserValidationResult(null, "Utente non trovato");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'interazione con il database per la ricerca utente: " + e.getMessage());
            return new UserValidationResult(null, "Errore durante l'interazione con il database");
        }
    }

    public synchronized int deleteUtente(int id) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "UPDATE UTENTE SET attivo = ? WHERE id_utente = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, false);
                ps.setInt(2, id);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0){
                    System.out.println("Utente con ID " + id + " disattivato con successo.");
                    return id;
                } else {
                    System.out.println("Errore durante la query per la disattivazione del utente.");
                    return 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la disattivazione del utente: " + e.getMessage());
            return 0;
        }
    }


    //oppure passi lista di prenotazioni effettuate e cancelli front end quelle che ti vengono passate
    //CONTROLLA-----------------------------------------------------------------------------------------------------------
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
                    return availableSlots;
                }
            }
        } catch (SQLException e) {
            return null;
        }
    }
//----------------------------------------------------------------------------------------------------------------------------
    public List<Prenotazione> getListaPrenotazioni() {
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
                System.out.println("Lista prenotazioni recuperata con successo");
                return prenotazioni;
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il recupero della lista prenotazioni: " + e.getMessage());
            return null;
        }
    }

    public List<Prenotazione> getListaPrenotazioniUtente(int idUtente) {     //controllare stato utente oppure no?
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
                System.out.println("Lista prenotazioni dell'utente recuperata con successo");
                return prenotazioni;
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il recupero della lista prenotazioni dell'utente: " + e.getMessage());
            return null;
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    public List<Prenotazione> getListaPrenotazioniDocente(){
        List<Prenotazione> prenotazioni = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "SELECT p.* " +
                    "FROM corso_docente cd " +
                    "JOIN docente d ON cd.id_docente = d.id_docente " +
                    "JOIN corso c ON cd.id_corso = c.id_corso " +
                    "LEFT JOIN prenotazioni p ON cd.id_corso_docente = p.id_corso_docente " +
                    "WHERE d.attivo = ?";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setBoolean(1, true);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Prenotazione prenotazione = new Prenotazione(rs.getInt("id_prenotazione"),
                                rs.getInt("id_utente"),
                                rs.getInt("id_corso_docente"),
                                rs.getString("data"),
                                rs.getInt("ora"),
                                rs.getString("stato"));
                        prenotazioni.add(prenotazione);
                    }
                    return prenotazioni;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
//---------------------------------------------------------------------------------------------------------------------------------------

    //check docente attivo
    public synchronized int setPrenotazione(int idUtente, int idCorsoDocente, String data, int ora) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "INSERT INTO Prenotazione (id_utente, id_corso_docente, data, ora, stato) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idUtente);
                ps.setInt(2, idCorsoDocente);
                ps.setString(3, data);
                ps.setInt(4, ora);
                ps.setString(5, "attiva");
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        System.out.println("Inserita prenotazione con successo");
                        return (int) rs.getLong(1);
                    } else {
                        System.out.println("Errore durante la query per l'inserimento della prenotazione.");
                        return 0;
                    }
                }else{
                    return 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'inserimento della prenotazione: " + e.getMessage());
            return 0;
        }
    }

    public synchronized int deletePrenotazione(int idPrenotazione) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "UPDATE CORSO SET stato = ? WHERE id_prenotazione = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, "disdetta");
                ps.setInt(2, idPrenotazione);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("Disdetta della prenotazione avvenuta con successo.");
                    return idPrenotazione;
                }else{
                    System.out.println("Errore durante la query per la disdetta della prenotazione.");
                    return 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la disdetta della prenotazione: " + e.getMessage());
            return 0;
        }
    }

    public synchronized int updatePrenotazione(int idPrenotazione) {
        try (Connection conn = DriverManager.getConnection(url1, user, password)) {
            String query = "UPDATE CORSO SET stato = ? WHERE id_prenotazione = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, "effettuata");
                ps.setInt(2, idPrenotazione);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("Aggiornamento della prenotazione avvenuta con successo.");
                    return idPrenotazione;
                }else{
                    System.out.println("Errore durante la query per l'aggiornamento della prenotazione.");
                    return 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante l'aggiornamento della prenotazione: " + e.getMessage());
            return 0;
        }
    }

}

package utils;

import DAO.Utente;

public class UserValidationResult {
    private Utente utente;
    private String errorMessage;

    public UserValidationResult(Utente utente, String errorMessage) {
        this.utente = utente;
        this.errorMessage = errorMessage;
    }

    public Utente getUtente() {
        return utente;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
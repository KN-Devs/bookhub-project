package fr.bookhub.exception;

public class LoansNotFoundException extends RuntimeException  {

    public LoansNotFoundException(int id) {
        super("Emprunt introuvable avec l'id : " + id);
    }
}

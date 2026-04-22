package fr.bookhub.exception;

public class ReservationNotFoundException extends RuntimeException  {

    public ReservationNotFoundException(int id) {
        super("Emprunt introuvable avec l'id : " + id);
    }
}


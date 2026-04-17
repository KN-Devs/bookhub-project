package fr.bookhub.exception;

public class UserBlockedByOverdueException extends RuntimeException {
  public UserBlockedByOverdueException(int userId) {
    super("L'utilisateur id=" + userId
            + " est bloqué : il a un emprunt en retard.");
  }
}
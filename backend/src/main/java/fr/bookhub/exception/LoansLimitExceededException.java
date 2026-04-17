package fr.bookhub.exception;

public class LoansLimitExceededException extends RuntimeException {
  public LoansLimitExceededException(int userId) {
    super("L'utilisateur id=" + userId
            + " a atteint la limite de 3 emprunts simultanés.");
  }
}
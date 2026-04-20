package fr.bookhub.exception;

public class BookAlreadyBorrowedException extends RuntimeException {
    public BookAlreadyBorrowedException(int bookId) {
        super("Le livre id=" + bookId + " est déjà emprunté.");
    }
}
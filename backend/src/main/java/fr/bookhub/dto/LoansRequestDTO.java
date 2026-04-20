package fr.bookhub.dto;

public class LoansRequestDTO {

    private int userId;
    private int bookId;
    // Pas de dueDate : durée fixe 14j calculée côté serveur (RG-LOAN-02)

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
}
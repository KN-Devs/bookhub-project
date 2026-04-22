package fr.bookhub.dto;

public class ReservationsRequestDTO {
    private int bookId;
    private int userId;

    // Getters et Setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
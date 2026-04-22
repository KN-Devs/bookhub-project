package fr.bookhub.dto;

public class ReviewsRequestDTO {
    private int bookId;
    private int userId;
    private int rating;
    private String comment;

    // Getters et Setters obligatoires
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
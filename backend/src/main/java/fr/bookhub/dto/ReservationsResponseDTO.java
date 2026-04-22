package fr.bookhub.dto;

import java.util.Date;

public class ReservationsResponseDTO {

    private int id;
    private int userId;
    private int bookId;
    private String bookTitle;
    private Date reservationDate;
    private int rankInLine;
    private String status;




    public String getBookTitle() {
        return bookTitle;
    }
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public Date getReservationDate() { return reservationDate; }
    public void setReservationDate(Date reservationDate) { this.reservationDate = reservationDate; }

    public int getRankInLine() { return rankInLine; }
    public void setRankInLine(int rankInLine) { this.rankInLine = rankInLine; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
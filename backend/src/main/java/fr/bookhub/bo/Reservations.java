package fr.bookhub.bo;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;


@Entity
@Table
public class Reservations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private  int id;

    private int userId;       // id_utilisateur

    private int bookId;       // id_livre

    @Column(nullable = false, length = 23)
    private Date ReservationDate;

    @Column(nullable = false, length = 10)
    private int rankInLine;

    @Column(length = 255)
    private String  status;

    public Reservations() {
    }

    public Reservations(int id, int userId, int bookId, Date reservationDate, int rankInLine, String status) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.ReservationDate = reservationDate;
        this.rankInLine = rankInLine;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Date getReservationDate() {
        return ReservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        ReservationDate = reservationDate;
    }

    public int getRankInLine() {
        return rankInLine;
    }

    public void setRankInLine(int rankInLine) {
        this.rankInLine = rankInLine;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Reservations that = (Reservations) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Reservations{" +
                "id=" + id +
                ", userId=" + userId +
                ", bookId=" + bookId +
                ", ReservationDate=" + ReservationDate +
                ", rankInLine=" + rankInLine +
                ", status='" + status + '\'' +
                '}';
    }
}

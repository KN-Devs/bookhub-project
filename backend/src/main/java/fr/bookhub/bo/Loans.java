package fr.bookhub.bo;

import jakarta.persistence.*;
import org.hibernate.Length;

import java.util.Date;
import java.util.Objects;

@Entity
@Table
public class Loans {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, length = 10)
    private int id;


    private int userId;       // id_utilisateur


    private int bookId;       // id_livre

    @Column(nullable = false, length = 23)
    private Date loanDate;    // date_debut

    @Column(nullable = false, length = 23)
    private Date dueDate;     // date_fin_prevue

    @Column(length = 23)
    private Date returnDate;  // date_retour_effective

    @Column(length = 255)
    private String status;    // statut


    public Loans() {
    }

    public Loans(int id, int userId, int bookId, Date loanDate, Date dueDate, Date returnDate, String status) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
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

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
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
        Loans loans = (Loans) o;
        return id == loans.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Loans{" +
                "id=" + id +
                ", userId=" + userId +
                ", bookId=" + bookId +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", status='" + status + '\'' +
                '}';
    }
}

package fr.bookhub.bo;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Reviews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne
    @JoinColumn(name = "book_id",  nullable = false)
    private Book book;

    @Column(nullable = false)
    private int rating;

    @Column(length = 255)
    private String comment;

    @Column(nullable = false)
    private boolean moderated = false;

    public Reviews() {}

    public Reviews(User user, Book book, int rating, String comment, boolean moderated) {
        this.user = user;
        this.book = book;
        this.rating = rating;
        this.comment = comment;
        this.moderated = moderated;
    }

    public Integer getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public boolean isModerated() { return moderated; }
    public void setModerated(boolean moderated) { this.moderated = moderated; }
}
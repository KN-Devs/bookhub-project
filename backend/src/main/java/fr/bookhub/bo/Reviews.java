package fr.bookhub.bo;


import jakarta.persistence.*;

@Entity
@Table
public class Reviews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, length = 10, unique = true)
    private int id;


    private int userId;       // id_utilisateur


    private int bookId;

    @Column(nullable = false,length = 10)
    private int rating;

    @Column(length = 10)
    private String comment;

    @Column(nullable = false, length = 3)
    private int isModarated;
}

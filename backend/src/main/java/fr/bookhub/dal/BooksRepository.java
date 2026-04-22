package fr.bookhub.dal;

import fr.bookhub.bo.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {

    // Spring comprend tout seul qu'il doit chercher par la colonne "isbn"
    Book findByIsbn(String isbn);

    @Query("SELECT AVG(r.rating) FROM Reviews r WHERE r.book.id = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") Integer bookId);

    @Query("SELECT COUNT(r) FROM Reviews r WHERE r.book.id = :bookId")
    Integer findReviewCountByBookId(@Param("bookId") Integer bookId);
}
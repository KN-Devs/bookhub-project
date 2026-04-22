package fr.bookhub.dal;

import fr.bookhub.bo.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {
    Book findByIsbn(@Param("isbn") String isbn);
    Book findById(@Param("id") int id);
}
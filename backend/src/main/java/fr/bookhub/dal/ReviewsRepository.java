package fr.bookhub.dal;

import fr.bookhub.bo.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewsRepository extends JpaRepository<Reviews, Integer> {

    List<Reviews> findByBookId(Integer bookId);

    Optional<Reviews> findByBookIdAndUserId(Integer bookId, Integer userId);

    boolean existsByBookIdAndUserId(Integer bookId, Integer userId);
}
package fr.bookhub.dal;

import fr.bookhub.bo.Loans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoansRepository extends JpaRepository<Loans, Integer> {

    List<Loans> findByUserId(int userId);
    List<Loans> findByBookId(int bookId);
    List<Loans> findByStatus(String status);

    boolean existsByBookIdAndStatus(int bookId, String status);
    long countByUserIdAndStatus(int userId, String status);
    boolean existsByUserIdAndStatus(int userId, String status);
}
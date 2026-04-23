package fr.bookhub.dal;

import fr.bookhub.bo.Reservations;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReservationsRepository extends JpaRepository<Reservations, Integer> {

    List<Reservations> findByUserId(int userId);
    List<Reservations> findByBookId(int bookId);

    // Rang dans la file d'attente
    int countByBookIdAndStatus(int bookId, String status);

    // nombre de réservation par user
    long countByUserIdAndStatus(int userId, String status);
    // Vérifier si l'user a déjà réservé ce livre
    boolean existsByUserIdAndBookIdAndStatus(int userId, int bookId, String status);

    List<Reservations> findByBookIdOrderByRankInLineAsc(int bookId);

    @Query("SELECT COALESCE(MAX(r.rankInLine), 0) FROM Reservations r WHERE r.bookId = :bookId")
    int findMaxRankByBookId(@Param("bookId") int bookId);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Reservations r SET r.rankInLine = r.rankInLine - 1
        WHERE r.bookId = :bookId AND r.rankInLine > :rank
    """)
    void shiftRanks(@Param("bookId") int bookId, @Param("rank") int rank);




}
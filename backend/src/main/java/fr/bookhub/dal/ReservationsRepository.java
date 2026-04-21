package fr.bookhub.dal;

import fr.bookhub.bo.Reservations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReservationsRepository extends JpaRepository<Reservations, Integer> {

    List<Reservations> findByUserId(int userId);
    List<Reservations> findByBookId(int bookId);

    // Rang dans la file d'attente
    int countByBookIdAndStatus(int bookId, String status);

    // Vérifier si l'user a déjà réservé ce livre
    boolean existsByUserIdAndBookIdAndStatus(int userId, int bookId, String status);
}
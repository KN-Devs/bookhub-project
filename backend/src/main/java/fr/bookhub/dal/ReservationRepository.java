package fr.bookhub.dal;

import fr.bookhub.bo.Reservations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservations, Integer> {

    List<Reservations> findByUserId(int userId);

    List<Reservations> findByBookId(int bookId);

    List<Reservations> findByStatus(String status);
}
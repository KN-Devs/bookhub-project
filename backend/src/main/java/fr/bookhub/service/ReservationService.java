// 📁 fr/bookhub/service/ReservationService.java
package fr.bookhub.service;

import fr.bookhub.bo.Reservations;
import fr.bookhub.dal.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;


    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservations createReservation(int userId, int bookId) {

        List<Reservations> existingReservations = reservationRepository.findByBookId(bookId);
        int rankInLine = existingReservations.size() + 1;

        Reservations reservation = new Reservations();
        reservation.setUserId(userId);
        reservation.setBookId(bookId);
        reservation.setReservationDate(new Date());
        reservation.setRankInLine(rankInLine);
        reservation.setStatus("EN_ATTENTE");

        return reservationRepository.save(reservation);
    }

    public List<Reservations> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservations> getReservationById(int id) {
        return reservationRepository.findById(id);
    }

    public List<Reservations> getReservationsByUser(int userId) {
        return reservationRepository.findByUserId(userId);
    }

    public Reservations updateStatus(int id, String newStatus) {
        Reservations reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable avec l'id : " + id));

        reservation.setStatus(newStatus);
        return reservationRepository.save(reservation);
    }

    public void cancelReservation(int id) {
        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Réservation introuvable avec l'id : " + id);
        }
        reservationRepository.deleteById(id);
    }
}
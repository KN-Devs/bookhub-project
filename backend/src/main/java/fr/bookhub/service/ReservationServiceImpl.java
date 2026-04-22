package fr.bookhub.service;

import fr.bookhub.bo.Reservations;
import fr.bookhub.dal.ReservationsRepository;
import fr.bookhub.dto.ReservationsRequestDTO;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationsRepository reservationsRepository;

    public ReservationServiceImpl(ReservationsRepository reservationsRepository) {
        this.reservationsRepository = reservationsRepository;
    }

    @Override
    public List<Reservations> getReservationsByUserId(Integer userId) {
        // Cette méthode doit exister dans votre ReservationsRepository
        return reservationsRepository.findByUserId(userId);
    }

    @Override
    public Reservations createReservation(ReservationsRequestDTO request) {
        Reservations reservation = new Reservations();
        reservation.setBookId(request.getBookId());
        reservation.setUserId(request.getUserId());
        reservation.setReservationDate(new Date());
        reservation.setStatus("EN_ATTENTE");
        reservation.setRankInLine(1);
        return reservationsRepository.save(reservation);
    }
}
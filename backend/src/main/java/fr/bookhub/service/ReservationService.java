package fr.bookhub.service;

import fr.bookhub.bo.Reservations;
import fr.bookhub.dto.ReservationsRequestDTO;
import java.util.List;

public interface ReservationService {
    List<Reservations> getReservationsByUserId(Integer userId);
    Reservations createReservation(ReservationsRequestDTO request);
}
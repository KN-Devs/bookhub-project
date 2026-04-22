package fr.bookhub.service;

import fr.bookhub.dto.LoansRequestDTO;
import fr.bookhub.dto.ReservationsRequestDTO;
import fr.bookhub.dto.ReservationsResponseDTO;

import java.util.List;

public interface ReservationService {

    ReservationsResponseDTO createReservation(ReservationsRequestDTO dto);
    ReservationsResponseDTO getReservationById(int id);
    List<ReservationsResponseDTO> getAllReservations();
    List<ReservationsResponseDTO> getReservationsByUser(int userId);
    List<ReservationsResponseDTO> getReservationsByStatus(String status);
    void deleteReservations(int id);
}

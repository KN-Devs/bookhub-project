package fr.bookhub.service;

import fr.bookhub.bo.Reservations;
import fr.bookhub.dal.BooksRepository;
import fr.bookhub.dal.ReservationsRepository;
import fr.bookhub.dto.LoansRequestDTO;
import fr.bookhub.dto.ReservationsRequestDTO;
import fr.bookhub.dto.ReservationsResponseDTO;
import fr.bookhub.exception.BookAlreadyBorrowedException;
import fr.bookhub.exception.LoansLimitExceededException;
import fr.bookhub.exception.ReservationNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationsServiceImpl implements ReservationService{

    private final ReservationsRepository reservationsRepository;
    private final BooksRepository bookRepository;


    public ReservationsServiceImpl(ReservationsRepository reservationsRepository,
                            BooksRepository bookRepository) {
        this.reservationsRepository = reservationsRepository;
        this.bookRepository = bookRepository;
    }



    @Override
    public ReservationsResponseDTO createReservation(ReservationsRequestDTO dto) {
        // RG-LOAN-01 : max 5 reservations simultanés
        long activeReservations = reservationsRepository
                .countByUserIdAndStatus(dto.getUserId(), "ACTIVE");
        if (activeReservations >= 5) {
            throw new LoansLimitExceededException(dto.getUserId());
        }

        boolean alreadyActive = reservationsRepository
                .existsByUserIdAndBookIdAndStatus(dto.getUserId(), dto.getBookId(),"ACTIVE");
        if (alreadyActive) {
            throw new BookAlreadyBorrowedException(dto.getBookId());
        }

        Reservations reservations = new Reservations();
        reservations.setReservationDate(new Date());
        reservations.setBookId(dto.getBookId());
        reservations.setStatus("EN_ATTENTE");
        reservations.setUserId(dto.getUserId());
        reservations.setRankInLine(1);


        return toDTO(reservationsRepository.save(reservations));
    }

    private ReservationsResponseDTO toDTO(Reservations reservations) {
        ReservationsResponseDTO dto = new ReservationsResponseDTO();
        dto.setId(reservations.getId());
        dto.setUserId(reservations.getUserId());
        dto.setBookId(reservations.getBookId());
        dto.setStatus(reservations.getStatus());
        dto.setReservationDate(reservations.getReservationDate());
        dto.setRankInLine(reservations.getRankInLine());
        dto.setBookTitle(bookRepository.findById(reservations.getBookId()).getTitle());

        return dto;
    };

    @Override
    public ReservationsResponseDTO getReservationById(int id) {
        return null;
    }

    @Override
    public List<ReservationsResponseDTO> getAllReservations() {
        return List.of();
    }

    @Override
    public List<ReservationsResponseDTO> getReservationsByUser(int userId) {
        return reservationsRepository.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationsResponseDTO> getReservationsByStatus(String status) {
        return List.of();
    }

    @Override
    public void deleteReservations(int id) {
        if (!reservationsRepository.existsById(id)) {
            throw new ReservationNotFoundException(id);
        }
        reservationsRepository.deleteById(id);
    }
}

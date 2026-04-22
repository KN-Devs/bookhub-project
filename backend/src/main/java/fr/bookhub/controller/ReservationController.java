package fr.bookhub.controller;

import fr.bookhub.bo.User;
import fr.bookhub.dal.BooksRepository;
import fr.bookhub.dal.ReservationsRepository;
import fr.bookhub.dal.UserRepository;
import fr.bookhub.dto.LoansRequestDTO;
import fr.bookhub.dto.LoansResponseDTO;
import fr.bookhub.dto.ReservationsRequestDTO;
import fr.bookhub.dto.ReservationsResponseDTO;
import fr.bookhub.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationsRepository reservationsRepository;
    private final BooksRepository bookRepository;
    private final ReservationService reservationService;
    private final UserRepository userRepository;


    public ReservationController(ReservationsRepository reservationsRepository, BooksRepository bookRepository, ReservationService reservationService, UserRepository userRepository) {
        this.reservationsRepository = reservationsRepository;
        this.bookRepository = bookRepository;
        this.reservationService = reservationService;
        this.userRepository = userRepository;
    }

    // POST /api/reservations — Créer une reservation
    @PostMapping
    public ResponseEntity<ReservationsResponseDTO> createReservations(
            @RequestBody ReservationsRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(dto));
    }

    // GET /api/reservations/my — Mes reservations (utilisateur connecté)
    @GetMapping("/my")
    public ResponseEntity<List<ReservationsResponseDTO>> getMyLoans(
            Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("l'utilsiateur est : " + user);
        return ResponseEntity.ok(
                reservationService.getReservationsByUser(user.getId())
        );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable int id) {
        reservationService.deleteReservations(id);
        return ResponseEntity.noContent().build();
    }


}

package fr.bookhub.controller;

import fr.bookhub.bo.Reservations;
import fr.bookhub.dto.ReservationsRequestDTO;
import fr.bookhub.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservationsController {

    private final ReservationService reservationService;

    // Utilisation de l'injection par constructeur (recommandé)
    public ReservationsController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // AJOUT DE CETTE MÉTHODE : Indispensable pour l'affichage Angular
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservations>> getByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationsRequestDTO request) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }
}
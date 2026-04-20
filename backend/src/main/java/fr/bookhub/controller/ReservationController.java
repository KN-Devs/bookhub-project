// 📁 fr/bookhub/controller/ReservationController.java
package fr.bookhub.controller;

import fr.bookhub.bo.Reservations;
import fr.bookhub.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<Reservations> create(@RequestParam int userId,
                                               @RequestParam int bookId) {
        Reservations created = reservationService.createReservation(userId, bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Reservations>> getAll() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservations> getById(@PathVariable int id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservations>> getByUser(@PathVariable int userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUser(userId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Reservations> updateStatus(@PathVariable int id,
                                                     @RequestParam String newStatus) {
        return ResponseEntity.ok(reservationService.updateStatus(id, newStatus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable int id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}
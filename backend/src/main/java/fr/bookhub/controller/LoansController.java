package fr.bookhub.controller;

import fr.bookhub.dto.LoansRequestDTO;
import fr.bookhub.dto.LoansResponseDTO;
import fr.bookhub.service.LoansService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoansController {

    private final LoansService loansService;

    public LoansController(LoansService loansService) {
        this.loansService = loansService;
    }

    // POST /api/loans — Créer un emprunt
    @PostMapping
    public ResponseEntity<LoansResponseDTO> createLoan(
            @RequestBody LoansRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loansService.createLoan(dto));
    }

    // GET /api/loans — Tous les emprunts (LIBRARIAN)
    @GetMapping
    public ResponseEntity<List<LoansResponseDTO>> getAllLoans() {
        return ResponseEntity.ok(loansService.getAllLoans());
    }

    // GET /api/loans/my — Mes emprunts (utilisateur connecté)
    @GetMapping("/my")
    public ResponseEntity<List<LoansResponseDTO>> getMyLoans(
            @RequestParam int userId) {
        // 🔔 userId sera extrait du JWT plus tard avec Spring Security
        return ResponseEntity.ok(loansService.getLoansByUser(userId));
    }

    // GET /api/loans/{id} — Un emprunt par ID
    @GetMapping("/{id}")
    public ResponseEntity<LoansResponseDTO> getLoanById(
            @PathVariable int id) {
        return ResponseEntity.ok(loansService.getLoanById(id));
    }

    // GET /api/loans/status/{status} — Par statut
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoansResponseDTO>> getLoansByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(loansService.getLoansByStatus(status));
    }

    // PUT /api/loans/{id}/return — Retourner un livre (LIBRARIAN)
    @PutMapping("/{id}/return")
    public ResponseEntity<LoansResponseDTO> returnBook(
            @PathVariable int id) {
        return ResponseEntity.ok(loansService.returnBook(id));
    }

    // DELETE /api/loans/{id} — Supprimer un emprunt
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable int id) {
        loansService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}
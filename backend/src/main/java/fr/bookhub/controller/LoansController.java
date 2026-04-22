package fr.bookhub.controller;

import fr.bookhub.bo.User;
import fr.bookhub.dal.LoansRepository;
import fr.bookhub.dal.UserRepository;
import fr.bookhub.dto.LoansRequestDTO;
import fr.bookhub.dto.LoansResponseDTO;
import fr.bookhub.service.LoansService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoansController {

    private final LoansService loansService;
    private final LoansRepository loansRepository;
    private final UserRepository userRepository;

    public LoansController(LoansService loansService, LoansRepository loansRepository, UserRepository userRepository) {
        this.loansService = loansService;
        this.loansRepository = loansRepository;
        this.userRepository = userRepository;
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
            Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("l'utilsiateur est : " + user);
        return ResponseEntity.ok(
                loansService.getLoansByUser(user.getId())
        );
    }

    // GET /api/loans/my/active — Nombre d'emprunts actifs de l'user connecté
    @GetMapping("/my/active")
    public ResponseEntity<Long> getMyActiveLoansCount(
            @AuthenticationPrincipal User currentUser) {
        long count = loansRepository.countByUserIdAndStatus(currentUser.getId(), "ACTIVE");
        return ResponseEntity.ok(count);
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

    // PUT /api/loans/{id}/return — Retourner un livre
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
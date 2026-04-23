package fr.bookhub.service;

import fr.bookhub.bo.Loans;
import fr.bookhub.dal.LoansRepository;
import fr.bookhub.dto.LoansRequestDTO;
import fr.bookhub.dto.LoansResponseDTO;
import fr.bookhub.dal.BooksRepository;
import fr.bookhub.dal.ReservationsRepository;
import fr.bookhub.bo.Reservations;
import fr.bookhub.bo.Book;
import fr.bookhub.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoansServiceImplTest {

    @Mock
    private LoansRepository loansRepository;


    @Mock
    private BooksRepository bookRepository;

    @Mock
    private ReservationsRepository reservationsRepository;

    @InjectMocks
    private LoansServiceImpl loansService;

    private LoansRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        // Préparation d'une requête de base valide
        validRequest = new LoansRequestDTO();
        validRequest.setUserId(1);
        validRequest.setBookId(10);
    }

    // =========================================================================
    // TESTS DE CRÉATION (CREATE)
    // =========================================================================

    @Test
    @DisplayName("Succès : Création d'un emprunt quand toutes les règles sont respectées")
    void should_create_loan_successfully() {
        // GIVEN : L'utilisateur est en règle
        when(loansRepository.countByUserIdAndStatus(1, "ACTIVE")).thenReturn(0L);
        when(loansRepository.existsByUserIdAndStatus(1, "OVERDUE")).thenReturn(false);
        when(loansRepository.existsByBookIdAndStatus(10, "ACTIVE")).thenReturn(false);

        Loans mockSavedLoan = new Loans();
        mockSavedLoan.setId(100);
        mockSavedLoan.setUserId(1);
        mockSavedLoan.setBookId(10);
        mockSavedLoan.setStatus("ACTIVE");
        when(loansRepository.save(any(Loans.class))).thenReturn(mockSavedLoan);

        // WHEN : On tente de créer l'emprunt
        LoansResponseDTO response = loansService.createLoan(validRequest);

        // THEN : On vérifie la solidité des données retournées
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100);
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
        assertThat(response.getUserId()).isEqualTo(1);

        // On vérifie que la BDD a bien été sollicitée pour l'enregistrement
        verify(loansRepository, times(1)).save(any(Loans.class));
    }

    @Test
    @DisplayName("Erreur : Limite de 3 emprunts atteinte (RG-LOAN-01)")
    void should_fail_when_limit_reached() {
        // GIVEN : L'utilisateur a déjà 3 emprunts
        when(loansRepository.countByUserIdAndStatus(1, "ACTIVE")).thenReturn(3L);

        // WHEN & THEN : On vérifie que l'exception spécifique est levée
        assertThatThrownBy(() -> loansService.createLoan(validRequest))
                .isInstanceOf(LoansLimitExceededException.class)
                .hasMessageContaining("a atteint la limite de 3 emprunts");

        verify(loansRepository, never()).save(any(Loans.class));
    }

    @Test
    @DisplayName("Erreur : Utilisateur bloqué par un retard (RG-LOAN-03)")
    void should_fail_when_user_has_overdue() {
        // GIVEN : Un retard est détecté en BDD
        when(loansRepository.countByUserIdAndStatus(1, "ACTIVE")).thenReturn(1L);
        when(loansRepository.existsByUserIdAndStatus(1, "OVERDUE")).thenReturn(true);

        // WHEN & THEN
        assertThatThrownBy(() -> loansService.createLoan(validRequest))
                .isInstanceOf(UserBlockedByOverdueException.class);
    }

    @Test
    @DisplayName("Erreur : Livre déjà emprunté par quelqu'un d'autre (RG-LOAN-04)")
    void should_fail_when_book_already_borrowed() {
        // GIVEN : Le livre est déjà marqué comme ACTIVE en BDD
        when(loansRepository.countByUserIdAndStatus(1, "ACTIVE")).thenReturn(0L);
        when(loansRepository.existsByUserIdAndStatus(1, "OVERDUE")).thenReturn(false);
        when(loansRepository.existsByBookIdAndStatus(10, "ACTIVE")).thenReturn(true);

        // WHEN & THEN
        assertThatThrownBy(() -> loansService.createLoan(validRequest))
                .isInstanceOf(BookAlreadyBorrowedException.class);
    }

    @Test
    void returnBook_shouldUpdateBookAndReservationStatus() {

        // 🔥 GIVEN
        Loans loan = new Loans();
        loan.setId(1);
        loan.setBookId(10);
        loan.setDueDate(new Date(System.currentTimeMillis() - 100000)); // en retard

        Book book = new Book();
        book.setId(10);
        book.setAvailableCopies(1);

        Reservations reservation = new Reservations();
        reservation.setId(5);
        reservation.setBookId(10);
        reservation.setRankInLine(1);
        reservation.setStatus("EN_ATTENTE");

        // mocks
        when(loansRepository.findById(1)).thenReturn(Optional.of(loan));
        when(reservationsRepository.findByBookIdOrderByRankInLineAsc(10))
                .thenReturn(List.of(reservation));

        // 🔥 WHEN
        loansService.returnBook(1);

        // 🔥 THEN

        // book stock augmenté
        assertEquals(2, book.getAvailableCopies());

        // réservation passée en DISPONIBLE
        assertEquals("DISPONIBLE", reservation.getStatus());

        verify(bookRepository).save(book);
        verify(reservationsRepository).save(reservation);
    }
}
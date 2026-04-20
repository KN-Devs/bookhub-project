package fr.bookhub.service;

import fr.bookhub.bo.Loans;
import fr.bookhub.dal.LoansRepository;
import fr.bookhub.dto.LoansRequestDTO;
import fr.bookhub.dto.LoansResponseDTO;
import fr.bookhub.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoansServiceImplTest {

    @Mock
    private LoansRepository loansRepository;

    @InjectMocks
    private LoansServiceImpl loansService;

    private LoansRequestDTO validRequest;
    private Loans activeLoan;
    private Loans returnedLoan;
    private Loans overdueLoan;

    @BeforeEach
    void setUp() {
        validRequest = new LoansRequestDTO();
        validRequest.setUserId(1);
        validRequest.setBookId(10);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 14);

        activeLoan = new Loans();
        activeLoan.setId(1);
        activeLoan.setUserId(1);
        activeLoan.setBookId(10);
        activeLoan.setLoanDate(new Date());
        activeLoan.setDueDate(cal.getTime());
        activeLoan.setStatus("ACTIVE");

        returnedLoan = new Loans();
        returnedLoan.setId(2);
        returnedLoan.setUserId(1);
        returnedLoan.setBookId(20);
        returnedLoan.setLoanDate(new Date());
        returnedLoan.setDueDate(cal.getTime());
        returnedLoan.setReturnDate(new Date());
        returnedLoan.setStatus("RETURNED");

        Calendar pastDue = Calendar.getInstance();
        pastDue.add(Calendar.DAY_OF_MONTH, -3);

        overdueLoan = new Loans();
        overdueLoan.setId(3);
        overdueLoan.setUserId(2);
        overdueLoan.setBookId(30);
        overdueLoan.setLoanDate(new Date());
        overdueLoan.setDueDate(pastDue.getTime());
        overdueLoan.setStatus("ACTIVE");
    }

    @Test
    @DisplayName("Succès : création d'un emprunt dans les règles")
    void createLoan_shouldSucceed_whenAllRulesRespected() {
        when(loansRepository.countByUserIdAndStatus(1, "ACTIVE")).thenReturn(0L);
        when(loansRepository.existsByUserIdAndStatus(1, "OVERDUE")).thenReturn(false);
        when(loansRepository.existsByBookIdAndStatus(10, "ACTIVE")).thenReturn(false);
        when(loansRepository.save(any(Loans.class))).thenReturn(activeLoan);

        LoansResponseDTO result = loansService.createLoan(validRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUserId()).isEqualTo(1);
        assertThat(result.getBookId()).isEqualTo(10);
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getLoanDate()).isNotNull();
        assertThat(result.getDueDate()).isNotNull();
        assertThat(result.getReturnDate()).isNull();
        verify(loansRepository, times(1)).save(any(Loans.class));
    }

    @Test
    @DisplayName("Échec : limite de 3 emprunts actifs atteinte (RG-LOAN-01)")
    void createLoan_shouldThrow_whenLoanLimitReached() {
        when(loansRepository.countByUserIdAndStatus(1, "ACTIVE")).thenReturn(3L);

        assertThatThrownBy(() -> loansService.createLoan(validRequest))
                .isInstanceOf(LoansLimitExceededException.class);

        verify(loansRepository, never()).save(any(Loans.class));
    }

    @Test
    @DisplayName("Échec : utilisateur bloqué par un retard en cours (RG-LOAN-03)")
    void createLoan_shouldThrow_whenUserHasOverdue() {
        when(loansRepository.countByUserIdAndStatus(1, "ACTIVE")).thenReturn(1L);
        when(loansRepository.existsByUserIdAndStatus(1, "OVERDUE")).thenReturn(true);

        assertThatThrownBy(() -> loansService.createLoan(validRequest))
                .isInstanceOf(UserBlockedByOverdueException.class);

        verify(loansRepository, never()).save(any(Loans.class));
    }

    @Test
    @DisplayName("Échec : livre déjà emprunté par quelqu'un d'autre (RG-LOAN-04)")
    void createLoan_shouldThrow_whenBookAlreadyBorrowed() {
        when(loansRepository.countByUserIdAndStatus(1, "ACTIVE")).thenReturn(0L);
        when(loansRepository.existsByUserIdAndStatus(1, "OVERDUE")).thenReturn(false);
        when(loansRepository.existsByBookIdAndStatus(10, "ACTIVE")).thenReturn(true);

        assertThatThrownBy(() -> loansService.createLoan(validRequest))
                .isInstanceOf(BookAlreadyBorrowedException.class);

        verify(loansRepository, never()).save(any(Loans.class));
    }

    @Test
    @DisplayName("Succès : récupération d'un emprunt par son ID")
    void getLoanById_shouldReturnLoan_whenFound() {
        when(loansRepository.findById(1)).thenReturn(Optional.of(activeLoan));

        LoansResponseDTO result = loansService.getLoanById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUserId()).isEqualTo(1);
        assertThat(result.getBookId()).isEqualTo(10);
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("Échec : emprunt introuvable par ID")
    void getLoanById_shouldThrow_whenNotFound() {
        when(loansRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loansService.getLoanById(999))
                .isInstanceOf(LoansNotFoundException.class);
    }

    @Test
    @DisplayName("Succès : récupération de tous les emprunts")
    void getAllLoans_shouldReturnAllLoans() {
        when(loansRepository.findAll()).thenReturn(List.of(activeLoan, returnedLoan, overdueLoan));

        List<LoansResponseDTO> result = loansService.getAllLoans();

        assertThat(result).hasSize(3);
        assertThat(result).extracting(LoansResponseDTO::getStatus)
                .containsExactlyInAnyOrder("ACTIVE", "RETURNED", "ACTIVE");
    }

    @Test
    @DisplayName("Succès : liste vide si aucun emprunt en base")
    void getAllLoans_shouldReturnEmptyList_whenNone() {
        when(loansRepository.findAll()).thenReturn(List.of());

        List<LoansResponseDTO> result = loansService.getAllLoans();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Succès : récupération des emprunts d'un utilisateur")
    void getLoansByUser_shouldReturnOnlyUserLoans() {
        when(loansRepository.findByUserId(1)).thenReturn(List.of(activeLoan, returnedLoan));

        List<LoansResponseDTO> result = loansService.getLoansByUser(1);

        assertThat(result).hasSize(2);
        assertThat(result).allSatisfy(dto -> assertThat(dto.getUserId()).isEqualTo(1));
    }

    @Test
    @DisplayName("Succès : liste vide si l'utilisateur n'a aucun emprunt")
    void getLoansByUser_shouldReturnEmptyList_whenNoLoansForUser() {
        when(loansRepository.findByUserId(999)).thenReturn(List.of());

        List<LoansResponseDTO> result = loansService.getLoansByUser(999);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Succès : récupération des emprunts par statut ACTIVE")
    void getLoansByStatus_shouldReturnMatchingLoans() {
        when(loansRepository.findByStatus("ACTIVE")).thenReturn(List.of(activeLoan, overdueLoan));

        List<LoansResponseDTO> result = loansService.getLoansByStatus("ACTIVE");

        assertThat(result).hasSize(2);
        assertThat(result).allSatisfy(dto -> assertThat(dto.getStatus()).isEqualTo("ACTIVE"));
    }

    @Test
    @DisplayName("Succès : liste vide si aucun emprunt ne correspond au statut")
    void getLoansByStatus_shouldReturnEmptyList_whenNoMatch() {
        when(loansRepository.findByStatus("RETURNED")).thenReturn(List.of());

        List<LoansResponseDTO> result = loansService.getLoansByStatus("RETURNED");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Succès : retour dans les délais → statut RETURNED")
    void returnBook_shouldSetStatusReturned_whenOnTime() {
        Calendar futureDue = Calendar.getInstance();
        futureDue.add(Calendar.DAY_OF_MONTH, 5);
        activeLoan.setDueDate(futureDue.getTime());

        when(loansRepository.findById(1)).thenReturn(Optional.of(activeLoan));
        when(loansRepository.save(any(Loans.class))).thenAnswer(inv -> inv.getArgument(0));

        LoansResponseDTO result = loansService.returnBook(1);

        assertThat(result.getStatus()).isEqualTo("RETURNED");
        assertThat(result.getReturnDate()).isNotNull();
        verify(loansRepository, times(1)).save(activeLoan);
    }

    @Test
    @DisplayName("Succès : retour en retard → statut OVERDUE")
    void returnBook_shouldSetStatusOverdue_whenLate() {
        Calendar pastDue = Calendar.getInstance();
        pastDue.add(Calendar.DAY_OF_MONTH, -5);
        overdueLoan.setDueDate(pastDue.getTime());

        when(loansRepository.findById(3)).thenReturn(Optional.of(overdueLoan));
        when(loansRepository.save(any(Loans.class))).thenAnswer(inv -> inv.getArgument(0));

        LoansResponseDTO result = loansService.returnBook(3);

        assertThat(result.getStatus()).isEqualTo("OVERDUE");
        assertThat(result.getReturnDate()).isNotNull();
    }

    @Test
    @DisplayName("Échec : retour d'un emprunt introuvable")
    void returnBook_shouldThrow_whenLoanNotFound() {
        when(loansRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loansService.returnBook(999))
                .isInstanceOf(LoansNotFoundException.class);

        verify(loansRepository, never()).save(any(Loans.class));
    }

    @Test
    @DisplayName("Succès : suppression d'un emprunt existant")
    void deleteLoan_shouldDelete_whenExists() {
        when(loansRepository.existsById(1)).thenReturn(true);
        doNothing().when(loansRepository).deleteById(1);

        loansService.deleteLoan(1);

        verify(loansRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Échec : suppression d'un emprunt introuvable")
    void deleteLoan_shouldThrow_whenNotFound() {
        when(loansRepository.existsById(999)).thenReturn(false);

        assertThatThrownBy(() -> loansService.deleteLoan(999))
                .isInstanceOf(LoansNotFoundException.class);

        verify(loansRepository, never()).deleteById(anyInt());
    }
}
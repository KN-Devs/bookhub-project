// 📁 src/test/java/fr/bookhub/service/ReservationServiceTest.java
package fr.bookhub.service;

import fr.bookhub.bo.Reservations;
import fr.bookhub.dal.ReservationRepository;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service de réservations")
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Reservations reservation1;
    private Reservations reservation2;
    private Reservations reservation3;


    @BeforeEach
    void setUp() {
        reservation1 = new Reservations(1, 10, 100, new Date(), 1, "EN_ATTENTE");
        reservation2 = new Reservations(2, 10, 101, new Date(), 1, "EN_ATTENTE");
        reservation3 = new Reservations(3, 20, 100, new Date(), 2, "CONFIRMEE");
    }




    @Test
    @DisplayName("createReservation — doit créer une réservation avec le bon rang et statut EN_ATTENTE")
    void createReservation_shouldCreateWithCorrectRankAndStatus() {

        when(reservationRepository.findByBookId(100)).thenReturn(List.of(reservation1, reservation3));
        when(reservationRepository.save(any(Reservations.class))).thenAnswer(inv -> inv.getArgument(0));


        Reservations result = reservationService.createReservation(99, 100);


        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(99);
        assertThat(result.getBookId()).isEqualTo(100);
        assertThat(result.getRankInLine()).isEqualTo(3);
        assertThat(result.getStatus()).isEqualTo("EN_ATTENTE");
        assertThat(result.getReservationDate()).isNotNull();
        verify(reservationRepository, times(1)).save(any(Reservations.class));
    }

    @Test
    @DisplayName("createReservation — doit être en rang 1 si aucune réservation n'existe pour ce livre")
    void createReservation_shouldBeRankOneIfNoExistingReservation() {

        when(reservationRepository.findByBookId(999)).thenReturn(List.of());
        when(reservationRepository.save(any(Reservations.class))).thenAnswer(inv -> inv.getArgument(0));

        Reservations result = reservationService.createReservation(10, 999);

        assertThat(result.getRankInLine()).isEqualTo(1);
    }




    @Test
    @DisplayName("getAllReservations — doit retourner toutes les réservations")
    void getAllReservations_shouldReturnAllReservations() {

        when(reservationRepository.findAll()).thenReturn(List.of(reservation1, reservation2, reservation3));


        List<Reservations> result = reservationService.getAllReservations();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(reservation1, reservation2, reservation3);
    }

    @Test
    @DisplayName("getAllReservations — doit retourner une liste vide s'il n'y a aucune réservation")
    void getAllReservations_shouldReturnEmptyListIfNone() {

        when(reservationRepository.findAll()).thenReturn(List.of());

        List<Reservations> result = reservationService.getAllReservations();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("getReservationById — doit retourner la réservation correspondante")
    void getReservationById_shouldReturnReservationWhenFound() {

        when(reservationRepository.findById(1)).thenReturn(Optional.of(reservation1));


        Optional<Reservations> result = reservationService.getReservationById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        assertThat(result.get().getUserId()).isEqualTo(10);
    }

    @Test
    @DisplayName("getReservationById — doit retourner Optional.empty() si l'ID n'existe pas")
    void getReservationById_shouldReturnEmptyWhenNotFound() {
        when(reservationRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Reservations> result = reservationService.getReservationById(999);

        assertThat(result).isEmpty();
    }



    @Test
    @DisplayName("getReservationsByUser — doit retourner uniquement les réservations de cet utilisateur")
    void getReservationsByUser_shouldReturnOnlyUserReservations() {

        when(reservationRepository.findByUserId(10)).thenReturn(List.of(reservation1, reservation2));


        List<Reservations> result = reservationService.getReservationsByUser(10);

        assertThat(result).hasSize(2);
        assertThat(result).allSatisfy(r -> assertThat(r.getUserId()).isEqualTo(10));
        assertThat(result).doesNotContain(reservation3);
    }

    @Test
    @DisplayName("getReservationsByUser — doit retourner une liste vide si l'utilisateur n'a pas de réservation")
    void getReservationsByUser_shouldReturnEmptyListIfNone() {

        when(reservationRepository.findByUserId(999)).thenReturn(List.of());

        List<Reservations> result = reservationService.getReservationsByUser(999);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("updateStatus — doit mettre à jour le statut correctement")
    void updateStatus_shouldUpdateStatusSuccessfully() {

        when(reservationRepository.findById(1)).thenReturn(Optional.of(reservation1));
        when(reservationRepository.save(any(Reservations.class))).thenAnswer(inv -> inv.getArgument(0));

        Reservations result = reservationService.updateStatus(1, "CONFIRMEE");

        assertThat(result.getStatus()).isEqualTo("CONFIRMEE");
        assertThat(result.getId()).isEqualTo(1);
        verify(reservationRepository, times(1)).save(reservation1);
    }

    @Test
    @DisplayName("updateStatus — doit lancer une exception si la réservation n'existe pas")
    void updateStatus_shouldThrowExceptionWhenNotFound() {
        when(reservationRepository.findById(999)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> reservationService.updateStatus(999, "CONFIRMEE"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("cancelReservation — doit supprimer la réservation existante")
    void cancelReservation_shouldDeleteSuccessfully() {

        when(reservationRepository.existsById(1)).thenReturn(true);
        doNothing().when(reservationRepository).deleteById(1);


        reservationService.cancelReservation(1);


        verify(reservationRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("cancelReservation — doit lancer une exception si la réservation n'existe pas")
    void cancelReservation_shouldThrowExceptionWhenNotFound() {

        when(reservationRepository.existsById(999)).thenReturn(false);


        assertThatThrownBy(() -> reservationService.cancelReservation(999))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("999");

        verify(reservationRepository, never()).deleteById(anyInt());
    }
}
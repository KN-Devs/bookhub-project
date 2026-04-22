import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReservationService } from '../../services/reservation-service';
import { AuthService } from '../../services/auth-service';
import { ReservationResponse } from '../../Interface/reservation';

@Component({
  selector: 'app-reservation-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reservation-view.html',
  styleUrl: './reservation-view.css'
})
export class ReservationView implements OnInit {

  reservations: ReservationResponse[] = [];
  erreur: string = '';
  succes: string = '';
  userId: number = 0;

  constructor(
    private reservationService: ReservationService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    this.userId = user?.userId;
    this.chargerReservations();
  }

  chargerReservations() {
    this.reservationService.getReservationsByUser(this.userId).subscribe({
      next: (data) => this.reservations = data,
      error: () => this.erreur = 'Impossible de charger vos réservations.'
    });
  }

  annulerReservation(id: number) {
    this.reservationService.cancelReservation(id).subscribe({
      next: () => {
        this.succes = 'Réservation annulée avec succès !';
        this.erreur = '';
        this.chargerReservations();
      },
      error: () => this.erreur = 'Erreur lors de l\'annulation.'
    });
  }
}

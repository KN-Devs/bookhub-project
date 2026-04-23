import {ChangeDetectorRef, Component} from '@angular/core';
import {BookService} from '../../services/book-service';
import {AuthService} from '../../services/auth-service';
import {LoanService} from '../../services/loan.service';
import {ReservationService} from '../../services/reservation.service';
import {ReviewsService} from '../../services/reviews';
import {DatePipe} from '@angular/common';
import {UserService} from '../../services/user-service';
import {User} from '../../Interface/user';

@Component({
  selector: 'app-account-view',
  imports: [
    DatePipe
  ],
  templateUrl: './account-view.html',
  styleUrl: './account-view.css',
})
export class AccountView {
  loansCount = 0;
  reservationsCount = 0;

  constructor(private bookService: BookService,
              private cdr: ChangeDetectorRef,
              private authService: AuthService,
              private userService : UserService,
              private reviewsService: ReviewsService) {
  }

  user: User | null = null;

  ngOnInit(): void {
    this.loadUser();
  }
  loadUser() {
    this.userService.getUserInfo().subscribe({
      next: (data) => {
        this.user = data;
        console.log("User info:", data);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error("Erreur chargement user:", err);
      }
    });
  }
  logout() {
    this.authService.logout();
  }
}

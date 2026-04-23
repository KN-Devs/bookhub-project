import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AuthService } from '../../services/auth-service';
import { LoanService } from '../../services/loan.service';
import { ReservationService } from '../../services/reservation.service';
import { BookService } from '../../services/book-service';
import { ReviewsService } from '../../services/reviews';


import { Observable } from 'rxjs';

// =========================
// INTERFACES
// =========================
interface Book {
  id?: number;
  title: string;
  author: string;
  isbn: string;
  description?: string;
  availableCopies: number;
  totalCopies: number;
  category?: { name: string };
}

interface Loan {
  bookId: number;
  status: string;
}

interface Reservation {
  bookId: number;
  status: string;
}

export interface Review {
  id?: number;
  rating: number;
  comment: string;
  userId: number;
  bookId: number;
}

// =========================
// COMPONENT
// =========================
@Component({
  selector: 'app-detail-book-view',
  standalone: true,
  imports: [CommonModule, FormsModule, DecimalPipe, RouterModule],
  templateUrl: './detail-book-view.html',
  styleUrls: ['./detail-book-view.css']
})
export class DetailBookView implements OnInit {

  book!: Book;
  reviews: Review[] = [];

  loans: Loan[] = [];
  reservations: Reservation[] = [];

  userId!: number;
  bookId!: number;

  selectedRating: number = 0;
  comment: string = '';
  hasCommented: boolean = false;

  averageRating: number = 0;
  averageRounded: number = 0;

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private loanService: LoanService,
    private reservationService: ReservationService,
    private reviewService: ReviewsService,
    private bookService: BookService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.bookId = Number(id);
    } else {
      console.error("ID de livre manquant dans l'URL");
    }

    this.initUser();
    this.loadBook();
    this.loadLoans();
    this.loadReservations();
    this.loadReviews();
  }

  // =========================
  // USER
  // =========================
  initUser(): void {
    const user = this.authService.getCurrentUser();
    if (user) this.userId = user.id;
  }

  // =========================
  // BOOK
  // =========================
  loadBook(): void {
    this.bookService.getBookById(this.bookId).subscribe({
      next: (data: Book) => {
        this.book = data;
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  // =========================
  // LOANS
  // =========================
  loadLoans(): void {
    this.loanService.getLoansByUser(this.userId).subscribe({
      next: (data: Loan[]) => this.loans = data
    });
  }

  loadReservations(): void {
    this.reservationService.getReservationsByUser(this.userId).subscribe({
      next: (data: Reservation[]) => this.reservations = data
    });
  }

  isBorrowed(bookId: number): boolean {
    return this.loans.some(l => l.bookId === bookId && l.status === 'ACTIVE');
  }

  isReserved(bookId: number): boolean {
    return this.reservations.some(r => r.bookId === bookId && r.status === 'ACTIVE');
  }

  // =========================
  // REVIEWS
  // =========================
  loadReviews(): void {
    this.reviewService.getReviewsByBook(this.bookId).subscribe({
      next: (data: Review[]) => {
        this.reviews = data;
        this.hasCommented = this.reviews.some(r => r.userId === this.userId);
        this.calculateAverage();
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  calculateAverage(): void {
    if (this.reviews.length === 0) {
      this.averageRating = 0;
      this.averageRounded = 0;
      return;
    }

    const sum = this.reviews.reduce((acc, r) => acc + r.rating, 0);
    this.averageRating = sum / this.reviews.length;
    this.averageRounded = Math.round(this.averageRating);
  }

  selectRating(star: number): void {
    this.selectedRating = star;
  }

  submitReview(): void {
    const review: Review = {
      rating: this.selectedRating,
      comment: this.comment,
      userId: this.userId,
      bookId: this.bookId
    };

    this.reviewService.addReview(review).subscribe({
      next: () => {
        this.comment = '';
        this.selectedRating = 0;
        this.loadReviews();
      },
      error: (err: any) => console.error(err)
    });
  }

  // =========================
  // ACTIONS
  // =========================
  emprunter(book: Book): void {
    console.log('Emprunter', book);
  }

  reserver(book: Book): void {
    console.log('Réserver', book);
  }
}

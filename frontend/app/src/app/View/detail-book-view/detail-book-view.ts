import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { Book } from '../../Interface/book';
import { BookService } from '../../services/book-service';
import { AuthService } from '../../services/auth-service';
import { Reviews } from '../../Interface/review';
import { ReviewsService } from '../../services/reviews';
import { LoanService } from '../../services/loan.service'; // Ajouté
import { ReservationService } from '../../services/reservation.service'; // Ajouté
import { LoanResponse } from '../../Interface/loan'; // Ajouté
import { ReservationResponse } from '../../Interface/reservation'; // Ajouté

@Component({
  selector: 'app-detail-book-view',
  imports: [
    RouterLink,
    FormsModule,
    DecimalPipe
  ],
  templateUrl: './detail-book-view.html',
  styleUrl: './detail-book-view.css',
})
export class DetailBookView implements OnInit {

  isbn: string = '';
  book!: Book;
  userId: number = 0;

  // Avis
  reviews: Reviews[] = [];
  averageRating: number = 0;
  averageRounded: number = 0;
  selectedRating: number = 0;
  comment: string = '';
  hasCommented: boolean = false;

  // État des emprunts et réservations
  loans: LoanResponse[] = [];
  reservations: ReservationResponse[] = [];

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private cdr: ChangeDetectorRef,
    private reviewsService: ReviewsService,
    private authService: AuthService,
    private loanService: LoanService,          // Injecté
    private reservationService: ReservationService // Injecté
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    this.userId = user?.userId;
    this.isbn = this.route.snapshot.paramMap.get('isbn')!;

    this.loadBook();
    this.chargerEmprunts();
    this.chargerReservations();
  }

  private loadBook(): void {
    this.bookService.getBookByIsbn(this.isbn).subscribe(data => {
      this.book = data;
      this.cdr.detectChanges();
      if (this.book.id) {
        this.loadReviews(this.book.id);
        const commented = localStorage.getItem(`commented_${this.book.id}`);
        if (commented) this.hasCommented = true;
      }
    });
  }

  private loadReviews(bookId: number): void {
    this.reviewsService.getReviewsByBook(bookId).subscribe(reviews => {
      this.reviews = reviews;
      this.cdr.detectChanges();
    });
    this.reviewsService.getAverageRating(bookId).subscribe(res => {
      this.averageRating = res.average;
      this.averageRounded = Math.round(res.average);
      this.cdr.detectChanges();
    });
  }

  // --- Actions pour les Avis ---
  selectRating(star: number): void {
    this.selectedRating = star;
  }

  submitReview(): void {
    if (!this.selectedRating || !this.comment.trim()) return;

    const review: Reviews = {
      bookId: this.book.id!,
      userId: this.userId,
      rating: this.selectedRating,
      comment: this.comment,
      moderated: false
    };

    this.reviewsService.addReview(review).subscribe({
      next: () => {
        this.hasCommented = true;
        localStorage.setItem(`commented_${this.book.id}`, 'true');

        // IMPORTANT : Recharger les données pour vider le "Aucun avis"
        this.loadReviews(this.book.id!);

        this.comment = '';
        this.selectedRating = 0;
        this.cdr.detectChanges(); // Force Angular à voir le changement
      },
      error: (err) => console.error("Erreur ajout avis :", err)
    });
  }

  // --- Logique Emprunts & Réservations ---
  chargerEmprunts() {
    this.loanService.getLoansByUser(this.userId).subscribe({
      next: (data) => {
        this.loans = data;
        this.cdr.detectChanges();
      },
    });
  }

  chargerReservations() {
    this.reservationService.getReservationsByUser(this.userId).subscribe({
      next: (data) => {
        this.reservations = data;
        this.cdr.detectChanges();
      },
    });
  }

  isBorrowed(bookId: number): boolean {
    return this.loans?.some(
      loan => loan.bookId === bookId && loan.status === 'ACTIVE'
    ) ?? false;
  }

  protected isReserved(bookId: number) {
    return this.reservations?.some(
      res => res.bookId === bookId && res.status === 'EN_ATTENTE'
    ) ?? false;
  }

  protected emprunter(book: Book): void {
    const loan = {
      userId: this.userId,
      bookId: book.id!
    };
    this.loanService.createLoan(loan).subscribe({
      next: () => {
        this.loadBook(); // Rafraîchir les infos du livre (ex: copies dispos)
        this.chargerEmprunts(); // Rafraîchir la liste des emprunts
      },
      error: (err) => console.error("Erreur emprunt :", err)
    });
  }

  protected reserver(book: Book): void {
    const reservation = {
      userId: this.userId,
      bookId: book.id!
    };
    this.reservationService.createReservation(reservation).subscribe({
      next: () => {
        this.loadBook();
        this.chargerReservations();
      },
      error: (err) => console.error("Erreur réservation :", err)
    });
  }
}

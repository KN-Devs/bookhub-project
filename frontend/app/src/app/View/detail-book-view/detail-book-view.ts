import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Book } from '../../Interface/book';
import { BookService } from '../../services/book-service';
import { StarRatingView } from '../star-rating-view/star-rating-view';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {ReviewService, ReviewResponse, ReviewRequest} from '../../services/reviews-service';
import { LoanService } from '../../services/loan-service';
import { ReservationService } from '../../services/reservation-service';
import { AuthService } from '../../services/auth-service'; // Import de l'AuthService

@Component({
  selector: 'app-detail-book-view',
  standalone: true,
  imports: [
    RouterLink,
    StarRatingView,
    DecimalPipe,
    FormsModule
  ],
  templateUrl: './detail-book-view.html',
  styleUrl: './detail-book-view.css',
})
export class DetailBookView implements OnInit {

  isbn: string = '';
  book!: Book;
  userId: number = 1;

  // Gestion des avis (Reviews)
  reviews: ReviewResponse[] = [];
  hasReviewed: boolean = false;
  showThankYouPopup: boolean = false;
  userRating: number = 0;
  userComment: string = '';

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private reviewService: ReviewService,
    private loanService: LoanService,
    private reservationService: ReservationService,
    public authService: AuthService, // Ajout en PUBLIC pour l'accès HTML (Librarian check)
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.isbn = this.route.snapshot.paramMap.get('isbn')!;

    this.bookService.getBookByIsbn(this.isbn).subscribe(data => {
      this.book = data;
      this.loadReviews();
    });
  }

  /**
   * Charge les avis liés au livre
   */
  private loadReviews(): void {
    if (!this.book?.id) return;

    this.reviewService.getReviewsByBook(this.book.id).subscribe(reviews => {
      this.reviews = reviews;
      this.cdr.detectChanges();
    });

    this.reviewService.hasUserReviewed(this.book.id, this.userId).subscribe(res => {
      this.hasReviewed = res.hasReviewed;
      this.cdr.detectChanges();
    });
  }

  /**
   * Envoi d'un avis/commentaire
   * Correction : Correspondance avec ton Controller Java (RequestBody + RequestParam)
   */
  // detail-book-view.ts
  protected submitReview(): void {
    if (this.hasReviewed || this.userRating === 0) return;

    const reviewData: ReviewRequest = {
      bookId: this.book.id!,
      rating: this.userRating,
      comment: this.userComment
    };

    // On envoie reviewData (Body) et userId (URL Param)
    this.reviewService.createReview(reviewData, this.userId).subscribe({
      next: (review: any) => {
        this.reviews.push(review);

        // Mise à jour de l'UI
        const total = this.reviews.reduce((sum, r) => sum + r.rating, 0);
        this.book.averageRating = total / this.reviews.length;
        this.book.reviewCount = this.reviews.length;

        this.hasReviewed = true;
        this.showThankYouPopup = true;
        this.userComment = '';
        this.userRating = 0;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur 500 détaillée :', err);
        alert("Erreur serveur. Vérifie dans ta base de données si l'utilisateur avec l'ID " + this.userId + " existe.");
      }
    });
  }

  /**
   * Action : Emprunter le livre
   */
  protected emprunter(book: Book): void {
    if (!book.id) return;

    this.loanService.createLoan({ bookId: book.id, userId: this.userId }).subscribe({
      next: () => {
        alert('Livre emprunté avec succès !');
        // Mise à jour visuelle du stock
        this.book.availableCopies--;
        if (this.book.availableCopies === 0) {
          this.book.available = false;
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        if (err.status === 409) {
          alert("Conflit : Vous avez déjà un emprunt actif pour ce livre.");
        } else {
          alert(err.error?.message || "Erreur lors de l'emprunt.");
        }
      }
    });
  }

  /**
   * Action : Réserver le livre (quand stock = 0)
   */
  protected reserver(book: Book): void {
    if (!book.id) return;

    this.reservationService.createReservation(this.userId, book.id).subscribe({
      next: () => {
        alert("Réservation réussie ! Tu es sur la liste d'attente.");
        this.cdr.detectChanges();
      },
      error: (err) => {
        alert(err.error?.message || "La réservation a échoué.");
      }
    });
  }
}

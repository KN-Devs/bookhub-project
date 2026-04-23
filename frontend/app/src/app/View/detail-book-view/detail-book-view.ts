import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {DecimalPipe} from '@angular/common';
import {Book} from '../../Interface/book';
import {BookService} from '../../services/book-service';
import {AuthService} from '../../services/auth-service';
import {Reviews} from '../../Interface/review';
import {ReviewsService} from '../../services/reviews';
import {LoanResponse} from '../../Interface/loan';
import {ReservationResponse} from '../../Interface/reservation';
import {LoanService} from '../../services/loan.service';
import {ReservationService} from '../../services/reservation.service';

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
  protected activeLoansCount: number = 0;
  userId: number = 0;

  reviews: Reviews[] = [];
  averageRating: number = 0;
  averageRounded: number = 0;
  selectedRating: number = 0;
  comment: string = '';
  hasCommented: boolean = false;
  loans: LoanResponse[] = [];
  reservations : ReservationResponse[] = [];

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private cdr: ChangeDetectorRef,
    private reviewsService: ReviewsService,
    private authService: AuthService,
    private loanService : LoanService,
    private reservationService : ReservationService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    this.userId = user?.userId;
    this.isbn = this.route.snapshot.paramMap.get('isbn')!;
    this.loadBook();
    this.loadActiveLoansCount();
    const user = this.authService.getCurrentUser();
    this.userId = user?.userId;
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
        this.loadReviews(this.book.id!);
        this.comment = '';
        this.selectedRating = 0;
      },
      error: (err) => console.error("Erreur ajout avis :", err)
    });
  }

  isBorrowed(bookId: number): boolean {
    return this.loans?.some(
      loan => loan.bookId === bookId && loan.status === 'ACTIVE'
    ) ?? false;
  }

  protected isReserved(bookId: number) {
    return this.reservations?.some(
      reservations => reservations.bookId === bookId && reservations.status === 'EN_ATTENTE'
    ) ?? false;
  }

  chargerEmprunts() {
    this.loanService.getLoansByUser(this.userId).subscribe({
      next: (data) => {
        this.loans = data;
        this.cdr.detectChanges();
        console.log("emprunts :", this.loans);
      },
    });
  }
  // @ts-ignore
  chargerReservations() {
    this.reservationService.getReservationsByUser(this.userId).subscribe({
      next: (data) => {
        this.reservations = data;
        this.cdr.detectChanges();
      },
    });
  }




  private loadActiveLoansCount(): void {
    this.bookService.getActiveLoansCount().subscribe({
      next: (count) => {
        this.activeLoansCount = count;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  protected emprunter(book: Book): void {
    this.bookService.borrowBook(book.isbn).subscribe({
      next: () => {
        this.loadBook();
        this.loadActiveLoansCount();
      },
      error: (err) => console.error(err)
    });
  }

  protected reserver(book: Book): void {
    this.bookService.reserveBook(book.isbn).subscribe({
      next: () => {
        this.loadBook();
        this.loadActiveLoansCount();
      },
      error: (err) => console.error(err)
    });
  }
}

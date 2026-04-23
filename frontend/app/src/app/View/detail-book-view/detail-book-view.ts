import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {DecimalPipe} from '@angular/common';
import {Book} from '../../Interface/book';
import {BookService} from '../../services/book-service';
import {Reviews} from '../../Interface/review';
import {ReviewsService} from '../../services/reviews';


@Component({
  selector: 'app-detail-book-view',
  imports: [
    RouterLink,
    FormsModule,   // ← pour [(ngModel)]
    DecimalPipe    // ← pour le pipe | number
  ],
  templateUrl: './detail-book-view.html',
  styleUrl: './detail-book-view.css',
})
export class DetailBookView implements OnInit {

  isbn: string = '';
  book!: Book;
  protected activeLoansCount: number = 0;

  // ⭐ Avis
  reviews: Reviews[] = [];
  averageRating: number = 0;
  averageRounded: number = 0;
  selectedRating: number = 0;
  comment: string = '';
  hasCommented: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private cdr: ChangeDetectorRef,
    private reviewsService: ReviewsService
  ) {}

  ngOnInit(): void {
    this.isbn = this.route.snapshot.paramMap.get('isbn')!;
    this.loadBook();
    this.loadActiveLoansCount();
  }

  private loadBook(): void {
    this.bookService.getBookByIsbn(this.isbn).subscribe(data => {
      this.book = data;
      this.cdr.detectChanges();
      // ⭐ Une fois le livre chargé, on charge les avis avec son id
      if (this.book.id) {
        this.loadReviews(this.book.id);
        // Vérifie si l'utilisateur a déjà commenté ce livre
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

  // ⭐ Sélection d'une étoile
  selectRating(star: number): void {
    this.selectedRating = star;
  }

  // ⭐ Poster un avis
  submitReview(): void {
    if (!this.selectedRating || !this.comment.trim()) return;

    const review: Reviews = {
      bookId: this.book.id!,
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

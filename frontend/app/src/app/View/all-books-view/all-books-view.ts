import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {Book} from '../../Interface/book';
import {BookService} from '../../services/book-service';
import {RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {DecimalPipe} from '@angular/common';
import {AuthService} from '../../services/auth-service';
import {LoanResponse} from '../../Interface/loan';
import {LoanService} from '../../services/loan.service';
import {ReservationService} from '../../services/reservation.service';
import {ReservationResponse} from '../../Interface/reservation';
import {ReviewsService} from '../../services/reviews';


@Component({
  selector: 'app-all-books-view',
  imports: [
    RouterLink,
    FormsModule,
    DecimalPipe  // ← pour le pipe | number
  ],
  templateUrl: './all-books-view.html',
  styleUrl: './all-books-view.css',
})
export class AllBooksView implements OnInit {

  public books: Book[] = [];
  public booksAffichage: Book[] = [];

  userId: number = 0;

  searchText = '';
  categoryId: number = 0;
  available: boolean | null = null;
  loans: LoanResponse[] = [];
  reservations: ReservationResponse[] = [];

  // ⭐ Notes moyennes par bookId
  averages: { [bookId: number]: number } = {};

  constructor(private bookService: BookService,
              private cdr: ChangeDetectorRef,
              private authService: AuthService,
              private loanService: LoanService,
              private reservationService: ReservationService,
              private reviewsService: ReviewsService) {
  }

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    this.userId = user?.userId;
    this.chargerEmprunts();
    this.chargerReservations();
    this.chargerLivre();
  }

  chargerLivre() {
    this.bookService.getAllBooks().subscribe({
      next: (data) => {
        this.books = data;
        this.booksAffichage = data;
        this.cdr.detectChanges();
        // ⭐ Charger la note moyenne de chaque livre
        this.books.forEach(book => {
          if (book.id) {
            this.reviewsService.getAverageRating(book.id).subscribe(res => {
              this.averages[book.id!] = res.average;
              this.cdr.detectChanges();
            });
          }
        });
      },
      error: (err) => {
        console.error("Erreur API:", err);
      }
    });
  }

  // ⭐ Arrondi pour les étoiles
  getAverageRounded(bookId: number): number {
    return Math.round(this.averages[bookId] || 0);
  }

  chargerEmprunts() {
    this.loanService.getLoansByUser(this.userId).subscribe({
      next: (data) => {
        this.loans = data;
        this.cdr.detectChanges();
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

  emprunter(book: Book) {
    const loan = {
      userId: this.userId!,
      bookId: book.id!
    };
    this.loanService.createLoan(loan).subscribe({
      next: (res) => {
        console.log("Emprunt réussi :", res);
        this.chargerEmprunts();
      },
      error: (err) => {
        console.error("Erreur emprunt :", err);
      }
    });
  }

  reserver(book: Book) {
    const reservation = {
      userId: this.userId!,
      bookId: book.id!
    };
    this.reservationService.createReservation(reservation).subscribe({
      next: (res) => {
        console.log("reservation réussi :", res);
        this.chargerReservations();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error("Erreur reservation :", err);
      }
    });
  }

  protected search() {
    this.booksAffichage = this.books;
    if (this.categoryId > 0) {
      this.booksAffichage = this.booksAffichage.filter(
        book => book.category?.id === this.categoryId
      );
    }
    if (this.available === true) {
      this.booksAffichage = this.booksAffichage.filter(
        book => book.availableCopies > 0
      );
    }
    if (this.available === false) {
      this.booksAffichage = this.booksAffichage.filter(
        book => book.availableCopies === 0
      );
    }
    const query = this.searchText.toLowerCase().trim();
    if (query.length > 0) {
      this.booksAffichage = this.booksAffichage.filter(book =>
        book.title.toLowerCase().includes(query) ||
        book.author.toLowerCase().includes(query) ||
        book.isbn.toLowerCase().includes(query)
      );
    }
    this.cdr.detectChanges();
  }
}

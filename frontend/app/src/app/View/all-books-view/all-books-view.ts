import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Book } from '../../Interface/book';
import { BookService } from '../../services/book-service';
import { LoanService } from '../../services/loan-service';
import { AuthService } from '../../services/auth-service';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { StarRatingView } from '../star-rating-view/star-rating-view';
import { ReservationService } from '../../services/reservation-service';

@Component({
  selector: 'app-all-books-view',
  standalone: true,
  imports: [RouterLink, FormsModule, DecimalPipe, StarRatingView],
  templateUrl: './all-books-view.html',
  styleUrl: './all-books-view.css',
})
export class AllBooksView implements OnInit {

  public books: Book[] = [];
  public booksAffichage: Book[] = [];
  searchText = '';
  categoryId: number = 0;
  available: boolean | null = null;

  // Correction de l'erreur TS2339 : On déclare le userId ici
  userId: number = 1;

  constructor(
    private bookService: BookService,
    private loanService: LoanService,
    private authService: AuthService,
    private reservationService: ReservationService, // Ajouté au constructeur
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.bookService.getAllBooks().subscribe({
      next: (data) => {
        this.books = data;
        this.booksAffichage = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error("Erreur API:", err)
    });
  }

  /**
   * Action : Emprunter un livre
   */
  protected emprunter(book: Book): void {
    if (!book.id) return;

    this.loanService.createLoan({ bookId: book.id, userId: this.userId }).subscribe({
      next: () => {
        alert(`L'emprunt de "${book.title}" a réussi !`);
        book.availableCopies--;
        this.cdr.detectChanges();
      },
      error: (err) => {

        if (err.status === 409) {
          alert(err.error?.message || "Vous avez déjà un emprunt en cours pour ce livre.");
        } else {
          const msg = err.error?.message || "Erreur lors de l'emprunt.";
          alert(msg);
        }
      }
    });
  }

  /**
   * Action : Réserver un livre
   */
  protected reserver(book: Book): void {
    if (!book.id) return;

    // Utilisation de ton service : userId d'abord, puis bookId
    this.reservationService.createReservation(this.userId, book.id).subscribe({
      next: () => {
        alert(`Réservation enregistrée pour "${book.title}".`);
        this.cdr.detectChanges();
      },
      error: (err) => {
        const msg = err.error?.message || "La réservation a échoué.";
        alert(msg);
      }
    });
  }


  protected search() {
    this.booksAffichage = [...this.books]; // On repart de la liste complète

    // Filtre par catégorie
    if (this.categoryId > 0) {
      this.booksAffichage = this.booksAffichage.filter(b => b.category?.id === this.categoryId);
    }

    // Filtre par disponibilité
    if (this.available === true) {
      this.booksAffichage = this.booksAffichage.filter(b => b.availableCopies > 0);
    } else if (this.available === false) {
      this.booksAffichage = this.booksAffichage.filter(b => b.availableCopies === 0);
    }

    // Filtre par texte (Titre, Auteur, ISBN)
    const query = this.searchText.toLowerCase().trim();
    if (query.length > 0) {
      this.booksAffichage = this.booksAffichage.filter(b =>
        b.title.toLowerCase().includes(query) ||
        b.author.toLowerCase().includes(query) ||
        b.isbn.toLowerCase().includes(query)
      );
    }

    this.cdr.detectChanges();
  }
}

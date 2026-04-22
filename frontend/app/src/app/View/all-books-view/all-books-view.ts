import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {Book} from '../../Interface/book';
import {BookService} from '../../services/book-service';
import {RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../../services/auth-service';
import {LoanResponse} from '../../Interface/loan';
import {LoanService} from '../../services/loan.service';

@Component({
  selector: 'app-all-books-view',
  imports: [
    RouterLink,
    FormsModule
  ],
  templateUrl: './all-books-view.html',
  styleUrl: './all-books-view.css',
})
export class AllBooksView implements OnInit {

  public books : Book[] = [];
  public booksAffichage : Book[] = [];

  userId: number = 0;

  searchText = '';
  categoryId: number = 0;
  available: boolean | null = null;
  loans: LoanResponse[] = [];


  constructor(private bookService : BookService,
              private cdr : ChangeDetectorRef,
              private authService: AuthService,
              private loanService: LoanService,) {
  }

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    this.userId = user?.userId;
    this.chargerEmprunts();
    this.chargerLivre();
  }

  chargerLivre(){
    this.bookService.getAllBooks().subscribe({
      next: (data) => {
        this.books = data;
        this.booksAffichage = data;
        this.cdr.detectChanges();
        console.log("les data:", data);
        console.log("Livres chargées:", this.books);
      },
      error: (err) => {
        console.error("Erreur API:", err);
      }
    });
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

  isBorrowed(bookId: number): boolean {
    return this.loans?.some(
      loan => loan.bookId === bookId && loan.status === 'ACTIVE'
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
    console.log("Réservation :", book.title);
  }

  protected search() {
    this.booksAffichage = this.books;
    // filtre catégorie
    if (this.categoryId >0) {
      this.booksAffichage = this.booksAffichage.filter(
        book => book.category?.id === this.categoryId
      );
    }
    // filtre disponibilité
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

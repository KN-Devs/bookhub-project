import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {Book} from '../../Interface/book';
import {BookService} from '../../services/book-service';
import {RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';

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

  searchText = '';
  categoryId: number = 0;
  available: boolean | null = null;


  constructor(private bookService : BookService, private cdr : ChangeDetectorRef) {
  }

  ngOnInit(): void {
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


  emprunter(book: Book) {
    console.log("Emprunt :", book.title);
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
    this.cdr.detectChanges();
  }
}

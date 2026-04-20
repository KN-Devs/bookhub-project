import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {Book} from '../../Interface/book';
import {BookService} from '../../services/book-service';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-all-books-view',
  imports: [
    RouterLink
  ],
  templateUrl: './all-books-view.html',
  styleUrl: './all-books-view.css',
})
export class AllBooksView implements OnInit {

  public books : Book[] = [];


  constructor(private bookService : BookService, private cdr : ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.bookService.getAllBooks().subscribe({
      next: (data) => {
        this.books = data;
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

}

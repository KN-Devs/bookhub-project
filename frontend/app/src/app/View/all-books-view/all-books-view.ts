import { Component } from '@angular/core';
import {Book} from '../../Interface/book';

@Component({
  selector: 'app-all-books-view',
  imports: [
  ],
  templateUrl: './all-books-view.html',
  styleUrl: './all-books-view.css',
})
export class AllBooksView {

  public books : Array<Book> = [
    {
      isbn: '978-0747532743',
      title: 'Harry Potter à l’école des sorciers',
      author: 'J.K. Rowling',
      category: 'Fantasy',
      availableCopies: 3,
      totalCopies: 5,
      averageRating: 4.8
    },
    {
      isbn: '978-0261103573',
      title: 'Le Seigneur des Anneaux',
      author: 'J.R.R. Tolkien',
      category: 'Fantasy',
      availableCopies: 1,
      totalCopies: 4,
      averageRating: 4.9
    },
    {
      isbn: '978-0132350884',
      title: 'Clean Code',
      author: 'Robert C. Martin',
      category: 'Programming',
      availableCopies: 2,
      totalCopies: 2,
      averageRating: 4.7
    }
  ];


  protected reserver(book: Book) {

  }

  protected getDetails(book: Book) {

  }
}

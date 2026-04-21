import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {Book} from '../../Interface/book';
import {BookService} from '../../services/book-service';

@Component({
  selector: 'app-detail-book-view',
  imports: [
    RouterLink
  ],
  templateUrl: './detail-book-view.html',
  styleUrl: './detail-book-view.css',
})
export class DetailBookView implements OnInit {

  isbn: string = "";
  book!: Book;
  protected activeLoansCount: number = 0;

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private cdr: ChangeDetectorRef
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

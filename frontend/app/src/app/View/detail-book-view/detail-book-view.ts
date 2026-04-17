import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {Book} from '../../Interface/book';
import {BookService} from '../../services/book-service';

@Component({
  selector: 'app-detail-book-view',
  imports: [
  ],
  templateUrl: './detail-book-view.html',
  styleUrl: './detail-book-view.css',
})
export class DetailBookView implements OnInit {

  constructor(private route: ActivatedRoute, private bookService: BookService, private cdr : ChangeDetectorRef) {
  }

  isbn: string = "";
  book!: Book;

  ngOnInit(): void {
    this.isbn = this.route.snapshot.paramMap.get('isbn')!;
    this.bookService.getBookByIsbn(this.isbn).subscribe(data => {
      this.book = data
      this.cdr.detectChanges()
    });

  }

}

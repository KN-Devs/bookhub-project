import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {Book} from '../../Interface/book';
import {BookService} from '../../services/book-service';
import {LoanResponse} from '../../Interface/loan';
import {ReservationResponse} from '../../Interface/reservation';
import {AuthService} from '../../services/auth-service';
import {LoanService} from '../../services/loan.service';
import {ReservationService} from '../../services/reservation.service';

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
  userId: number = 0;
  loans: LoanResponse[] = [];
  reservations : ReservationResponse[] = [];

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private cdr: ChangeDetectorRef,
    private authService : AuthService,
    private loanService : LoanService,
    private reservationService : ReservationService
  ) {}

  ngOnInit(): void {
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

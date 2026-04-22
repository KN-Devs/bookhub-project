import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Book } from '../Interface/book';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BookService {

  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getAllBooks(): Observable<Book[]> {
    return this.http.get<Book[]>(`${this.apiUrl}/books`);
  }

  getBookByIsbn(isbn: string): Observable<Book> {
    return this.http.get<Book>(`${this.apiUrl}/books/${isbn}`);
  }

  getBookById(id: number): Observable<Book> {
    return this.http.get<Book>(`${this.apiUrl}/books/id/${id}`);
  }

  createBook(requestBody: Book): Observable<Book> {
    if (!requestBody.title || requestBody.title.trim() === '') {
      throw new Error('Le titre est obligatoire');
    }
    if (!requestBody.author || requestBody.author.trim() === '') {
      throw new Error("L'auteur est obligatoire");
    }
    if (!requestBody.isbn || requestBody.isbn.trim() === '') {
      throw new Error('ISBN obligatoire');
    }
    if (requestBody.availableCopies != null && requestBody.availableCopies < 0) {
      throw new Error('Les copies disponibles ne peuvent pas être négatives');
    }
    if (requestBody.totalCopies != null && requestBody.totalCopies < 0) {
      throw new Error('Les copies totales ne peuvent pas être négatives');
    }
    if (
      requestBody.availableCopies != null &&
      requestBody.totalCopies != null &&
      requestBody.availableCopies > requestBody.totalCopies
    ) {
      throw new Error('Copies disponibles > copies totales interdit');
    }

    return this.http.post<Book>(`${this.apiUrl}/books`, requestBody);
  }

  updateBook(isbn: string, requestBody: Book): Observable<Book> {
    if (!isbn) {
      throw new Error('ISBN obligatoire');
    }
    if (requestBody.title !== undefined && requestBody.title.trim() === '') {
      throw new Error('Titre invalide');
    }
    if (requestBody.availableCopies != null && requestBody.availableCopies < 0) {
      throw new Error('Copies disponibles invalides');
    }
    if (
      requestBody.totalCopies != null &&
      requestBody.availableCopies != null &&
      requestBody.availableCopies > requestBody.totalCopies
    ) {
      throw new Error('Incohérence des copies');
    }

    return this.http.put<Book>(`${this.apiUrl}/books/${isbn}`, requestBody);
  }

  borrowBook(isbn: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/books/${isbn}/borrow`, {});
  }

  reserveBook(isbn: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/books/${isbn}/reserve`, {});
  }

  getActiveLoansCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/loans/my/active`);
  }
}

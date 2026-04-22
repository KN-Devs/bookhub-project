import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Book} from '../Interface/book';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BookService {
  // CORRECTION : Initialisation de l'URL de base du backend
  private apiUrl = 'http://localhost:8080/api/books';

  constructor(private http: HttpClient) {
  }

  getAllBooks(): Observable<Book[]> {
    return this.http.get<Book[]>(this.apiUrl);
  };

  getBookByIsbn(isbn: string): Observable<Book> {
    return this.http.get<Book>(`${this.apiUrl}/isbn/${isbn}`);
  }

  getBookById(id: number): Observable<Book> {
    return this.http.get<Book>(`${this.apiUrl}/id/${id}`);
  }

  createBook(requestBody: Book): Observable<Book> {
    // Vérifications front
    if (!requestBody.title || requestBody.title.trim() === '') {
      throw new Error('Le titre est obligatoire');
    }
    if (!requestBody.author || requestBody.author.trim() === '') {
      throw new Error('L’auteur est obligatoire');
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

    // Appel API avec l'URL centralisée
    return this.http.post<Book>(this.apiUrl, requestBody);
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
    return this.http.put<Book>(`${this.apiUrl}/${isbn}`, requestBody);
  }
}

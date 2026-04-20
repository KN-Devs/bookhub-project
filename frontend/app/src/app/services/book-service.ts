import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Book} from '../Interface/book';

@Injectable({
  providedIn: 'root',
})
export class BookService {

  constructor(private http: HttpClient) {
  }

  getAllBooks() {
    return this.http.get<Book[]>('http://localhost:8080/api/books');
  };

  getBookByIsbn(isbn: string) {
    return this.http.get<Book>('http://localhost:8080/api/books/' + isbn);
  }

  createBook(requestBody: Book) {
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
    // appel API
    return this.http.post<Book>('http://localhost:8080/api/books', requestBody);
  }


  updateBook(id: number, requestBody: Book) {
    if (!id) {
      throw new Error('ID obligatoire');
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
    return this.http.put<Book>(`http://localhost:8080/api/books/${id}`, requestBody);
  }


}

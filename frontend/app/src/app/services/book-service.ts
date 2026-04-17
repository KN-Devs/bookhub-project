import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Book} from '../Interface/book';

@Injectable({
  providedIn: 'root',
})
export class BookService {

  constructor(private http : HttpClient) {
  }

   getAllBooks(){
    return this.http.get<Book[]>('http://localhost:8080/api/books');
  };

  getBookByIsbn(isbn: string) {
    return this.http.get<Book>('http://localhost:8080/api/books/' + isbn);
  }



}

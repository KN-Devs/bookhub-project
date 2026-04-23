import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {Reviews} from '../Interface/review';
import {Review} from '../View/detail-book-view/detail-book-view';


@Injectable({ providedIn: 'root' })
export class ReviewsService {

  private apiUrl = 'http://localhost:8080/api/reviews';

  constructor(private http: HttpClient) {}

  getReviewsByBook(bookId: number): Observable<Reviews[]> {
    return this.http.get<Reviews[]>(`${this.apiUrl}/book/${bookId}`);
  }

  getAverageRating(bookId: number): Observable<{ average: number }> {
    return this.http.get<{ average: number }>(`${this.apiUrl}/book/${bookId}/average`);
  }

  addReview(review: Review): Observable<Reviews> {
    return this.http.post<Reviews>(this.apiUrl, review);
  }
}

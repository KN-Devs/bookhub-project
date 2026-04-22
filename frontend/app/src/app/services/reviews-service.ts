import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ReviewResponse {
  id: number;
  username: string;
  rating: number;
  comment: string;
  moderated: boolean;
}

export interface ReviewRequest {
  bookId: number;
  rating: number;
  comment: string;
}

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private apiUrl = 'http://localhost:8080/api/reviews';

  constructor(private http: HttpClient) {}

  getReviewsByBook(bookId: number): Observable<ReviewResponse[]> {
    return this.http.get<ReviewResponse[]>(`${this.apiUrl}/book/${bookId}`);
  }

  hasUserReviewed(bookId: number, userId: number): Observable<{ hasReviewed: boolean }> {
    return this.http.get<{ hasReviewed: boolean }>(
      `${this.apiUrl}/check?bookId=${bookId}&userId=${userId}`
    );
  }

  createReview(dto: ReviewRequest, userId: number): Observable<ReviewResponse> {
    return this.http.post<ReviewResponse>(`${this.apiUrl}?userId=${userId}`, dto);
  }
}

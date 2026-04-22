import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReservationResponse } from '../Interface/reservation';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = 'http://localhost:8080/api/reservations';

  constructor(private http: HttpClient) {}

  // CORRECTION : Envoi d'un objet JSON au lieu de paramètres URL
  createReservation(userId: number, bookId: number): Observable<ReservationResponse> {
    const body = { userId, bookId };
    return this.http.post<ReservationResponse>(this.apiUrl, body);
  }

  getAllReservations(): Observable<ReservationResponse[]> {
    return this.http.get<ReservationResponse[]>(this.apiUrl);
  }

  getReservationById(id: number): Observable<ReservationResponse> {
    return this.http.get<ReservationResponse>(`${this.apiUrl}/${id}`);
  }

  getReservationsByUser(userId: number): Observable<ReservationResponse[]> {
    return this.http.get<ReservationResponse[]>(`${this.apiUrl}/user/${userId}`);
  }

  updateStatus(id: number, newStatus: string): Observable<ReservationResponse> {
    return this.http.patch<ReservationResponse>(
      `${this.apiUrl}/${id}/status?newStatus=${newStatus}`, {}
    );
  }

  cancelReservation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

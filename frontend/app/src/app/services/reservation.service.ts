import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ReservationRequest, ReservationResponse } from '../Interface/reservation';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {

  private apiUrl = 'http://localhost:8080/api/reservations';

  constructor(private http: HttpClient) {}

  createReservation(userId: number, bookId: number) {
    return this.http.post<ReservationResponse>(
      `${this.apiUrl}?userId=${userId}&bookId=${bookId}`, {}
    );
  }

  getAllReservations() {
    return this.http.get<ReservationResponse[]>(this.apiUrl);
  }

  getReservationById(id: number) {
    return this.http.get<ReservationResponse>(`${this.apiUrl}/${id}`);
  }

  getReservationsByUser(userId: number) {
    return this.http.get<ReservationResponse[]>(`${this.apiUrl}/user/${userId}`);
  }

  updateStatus(id: number, newStatus: string) {
    return this.http.patch<ReservationResponse>(
      `${this.apiUrl}/${id}/status?newStatus=${newStatus}`, {}
    );
  }

  cancelReservation(id: number) {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

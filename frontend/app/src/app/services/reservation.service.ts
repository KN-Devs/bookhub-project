import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ReservationRequest, ReservationResponse } from '../Interface/reservation';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {

  private apiUrl = 'http://localhost:8080/api/reservations';

  constructor(private http: HttpClient) {}

  createReservation(request : ReservationRequest) {
    return this.http.post<ReservationResponse>(this.apiUrl, request);
  }

  getAllReservations() {
    return this.http.get<ReservationResponse[]>(this.apiUrl);
  }

  getReservationById(id: number) {
    return this.http.get<ReservationResponse>(`${this.apiUrl}/${id}`);
  }

  getReservationsByUser(userId: number) {
    return this.http.get<ReservationResponse[]>(`${this.apiUrl}/my`);
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

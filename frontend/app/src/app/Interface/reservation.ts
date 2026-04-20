export interface ReservationRequest {
  userId: number;
  bookId: number;
}

export interface ReservationResponse {
  id: number;
  userId: number;
  bookId: number;
  reservationDate: string;
  rankInLine: number;
  status: string;
}

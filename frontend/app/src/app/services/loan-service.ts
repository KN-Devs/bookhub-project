import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LoanRequest, LoanResponse } from '../Interface/loan';

@Injectable({
  providedIn: 'root'
})
export class LoanService {

  private apiUrl = 'http://localhost:8080/api/loans';

  constructor(private http: HttpClient) {}

  createLoan(request: LoanRequest) {
    return this.http.post<LoanResponse>(this.apiUrl, request);
  }

  getAllLoans() {
    return this.http.get<LoanResponse[]>(this.apiUrl);
  }

  getLoanById(id: number) {
    return this.http.get<LoanResponse>(`${this.apiUrl}/${id}`);
  }

  getLoansByUser(userId: number) {
    return this.http.get<LoanResponse[]>(`${this.apiUrl}/my?userId=${userId}`);
  }

  getLoansByStatus(status: string) {
    return this.http.get<LoanResponse[]>(`${this.apiUrl}/status/${status}`);
  }

  returnBook(id: number) {
    return this.http.put<LoanResponse>(`${this.apiUrl}/${id}/return`, {});
  }

  deleteLoan(id: number) {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }


}

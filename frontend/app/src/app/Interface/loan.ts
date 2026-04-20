export interface LoanRequest {
  userId: number;
  bookId: number;
}

export interface LoanResponse {
  id: number;
  userId: number;
  bookId: number;
  loanDate: string;
  dueDate: string;
  returnDate?: string;
  status: string;
}

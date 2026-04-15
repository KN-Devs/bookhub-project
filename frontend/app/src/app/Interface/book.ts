export interface Book {
  title: string;
  author: string;
  isbn: string;
  category?: string;
  description?: string;
  coverUrl?: string;
  availableCopies?: number;
  totalCopies?: number;
  averageRating?: number;
}

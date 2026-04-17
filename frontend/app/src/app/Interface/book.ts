export interface Book {
  id?: number;
  title: string;
  author: string;
  isbn: string;
  category?: string;
  description?: string;
  coverUrl?: string;
  availableCopies: number;
  totalCopies: number;
  averageRating?: number;
  createdAt?: string;
  isAvailable?: boolean;
  reservedCopies?: number;
}

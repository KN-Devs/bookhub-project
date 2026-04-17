export interface Book {
  id?: number;
  title: string;
  author: string;
  isbn: string;
  description?: string;
  coverImage?: string;
  available: boolean;
  availableCopies?: number;
  totalCopies?: number;
  createdAt?: string;
  category?: {
    id: number;
    name: string;
  };
  averageRating?: number;
}

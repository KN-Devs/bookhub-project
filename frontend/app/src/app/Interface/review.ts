export interface Reviews {
  id?: number;
  bookId: number;
  userId: number;
  rating: number;
  comment: string;
  moderated: boolean;
}

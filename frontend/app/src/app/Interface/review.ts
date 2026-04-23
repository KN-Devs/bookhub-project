export interface Reviews {
  id?: number;
  bookId: number;
  rating: number;
  comment: string;
  moderated: boolean;
}

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  roles: string;
  createdAt: string; // ou Date si tu transforms côté front
  active: boolean;
}

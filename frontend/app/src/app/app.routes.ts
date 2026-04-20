import { Routes } from '@angular/router';
import { ConnectionView } from './View/connection-view/connection-view';
import { AllBooksView } from './View/all-books-view/all-books-view';
import { RegisterView } from './View/register-view/register-view';
import { DetailBookView } from './View/detail-book-view/detail-book-view';
import { LoanView } from './View/loan-view/loan-view';  // ← ajouter
import { AuthGuard } from './guards/auth.guard';
import {ReservationView} from './View/reservation-view/reservation-view';

export const routes: Routes = [
  { path: '', redirectTo: '/connection', pathMatch: 'full' },
  { path: 'connection', component: ConnectionView },
  { path: 'register', component: RegisterView },
  { path: 'viewBooks', component: AllBooksView, canActivate: [AuthGuard] },
  { path: 'viewBooks/detail/:isbn', component: DetailBookView, canActivate: [AuthGuard] },
  { path: 'loans', component: LoanView, canActivate: [AuthGuard] },
  { path: 'reservations', component: ReservationView, canActivate: [AuthGuard] },
];

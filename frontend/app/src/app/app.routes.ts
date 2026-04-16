import { Routes } from '@angular/router';
import { ConnectionView } from './View/connection-view/connection-view';
import { AllBooksView } from './View/all-books-view/all-books-view';
import { RegisterView } from './View/register-view/register-view';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: ConnectionView },
  { path: 'viewBooks', component: AllBooksView, canActivate: [authGuard] },
  { path: 'register', component: RegisterView },
];

import { Routes } from '@angular/router';
import {ConnectionView} from './View/connection-view/connection-view';
import {AllBooksView} from './View/all-books-view/all-books-view';
import {RegisterView} from './View/register-view/register-view';
import {DetailBookView} from './View/detail-book-view/detail-book-view';

export const routes: Routes = [
  { path: '', component: ConnectionView },
  { path: 'viewBooks', component: AllBooksView },
  { path: 'register', component: RegisterView },
  { path: 'viewBooks/detail/:isbn', component: DetailBookView },
];

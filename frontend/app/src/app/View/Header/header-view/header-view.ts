import { Component } from '@angular/core';
import {SearchView} from '../search-view/search-view';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../../services/auth-service';

@Component({
  selector: 'app-header-view',
  imports: [
    RouterLink
  ],
  templateUrl: './header-view.html',
  styleUrl: './header-view.css',
})
export class HeaderView {


  constructor(private authService : AuthService, private router : Router) {

  }


  protected logout() {
    this.authService.logout();
    this.router.navigate(['/connection']);
  }

  protected readonly SearchView = SearchView;
}

import { Component } from '@angular/core';
import {SearchView} from '../search-view/search-view';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-header-view',
  imports: [
    SearchView,
    RouterLink
  ],
  templateUrl: './header-view.html',
  styleUrl: './header-view.css',
})
export class HeaderView {


  protected logout() {

  }
}

import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HeaderView} from '../Header/header-view/header-view';
import {FooterView} from '../Footer/footer-view/footer-view';
import {Router} from '@angular/router';

@Component({
  selector: 'app-connection-view',
  imports: [
    FormsModule,
  ],
  templateUrl: './connection-view.html',
  styleUrl: './connection-view.css',
})
export class ConnectionView {


  constructor(private router : Router) {
  }

  protected login() {
    this.router.navigate(["viewBooks"]);

  }

  protected register() {
    this.router.navigate(["register"]);
  }
}

import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {FooterView} from './View/Footer/footer-view/footer-view';
import {HeaderView} from './View/Header/header-view/header-view';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, FooterView, HeaderView],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('Frontend-Angular');
}

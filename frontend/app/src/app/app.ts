import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ApiService } from './services/api';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  standalone: true,
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {

  message: string = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getTest().subscribe(data => {
      console.log('MESSAGE AVANT:', this.message);
      this.message = data;
      console.log('MESSAGE APRES:', this.message);
      console.log(data);
      console.log("API CALL lancé");
    });
  }
}

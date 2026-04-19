import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService, LoginRequest } from '../../services/auth-service';

@Component({
  selector: 'app-connection-view',
  imports: [
    FormsModule,
    CommonModule
  ],
  templateUrl: './connection-view.html',
  styleUrl: './connection-view.css',
})
export class ConnectionView implements OnInit {
  email: string = '';
  password: string = '';
  loading: boolean = false;
  submitted: boolean = false;
  error: string = '';

  constructor(
    private router: Router,
    private authService: AuthService
  ) {
    // Rediriger vers les livres si déjà connecté
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['viewBooks']);
    }
  }

  ngOnInit(): void {
  }

  onLogin(): void {
    this.submitted = true;
    this.error = '';

    if (this.email && this.password) {
      this.loading = true;

      const loginRequest: LoginRequest = {
        email: this.email,
        password: this.password
      };

      this.authService.login(loginRequest).subscribe({
        next: (response) => {
          this.loading = false;
          this.router.navigate(['viewBooks']);
        },
        error: (err) => {
          this.loading = false;
          this.error = 'Email ou mot de passe incorrect';
          console.error('Login error:', err);
        }
      });
    }
  }

  onRegister(): void {
    this.router.navigate(['register']);
  }
}

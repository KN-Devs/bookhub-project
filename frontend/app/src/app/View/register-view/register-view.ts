import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService, RegisterRequest } from '../../services/auth-service';

@Component({
  selector: 'app-register-view',
  imports: [FormsModule, CommonModule],
  templateUrl: './register-view.html',
  styleUrl: './register-view.css',
})
export class RegisterView {
  lastName: string = '';
  firstName: string = '';
  email: string = '';
  password: string = '';
  confirmPassword: string = '';
  phone: string = '';
  
  loading: boolean = false;
  submitted: boolean = false;
  error: string = '';
  successMessage: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onRegister(): void {
    this.submitted = true;
    this.error = '';
    this.successMessage = '';

    // Validation basique
    if (!this.lastName || !this.firstName || !this.email || !this.password || !this.confirmPassword) {
      this.error = 'Tous les champs requis doivent être remplis';
      return;
    }

    if (this.password !== this.confirmPassword) {
      this.error = 'Les mots de passe ne correspondent pas';
      return;
    }

    if (this.password.length < 6) {
      this.error = 'Le mot de passe doit contenir au moins 6 caractères';
      return;
    }

    this.loading = true;

    const registerRequest: RegisterRequest = {
      lastName: this.lastName,
      firstName: this.firstName,
      email: this.email,
      password: this.password,
      confirmPassword: this.confirmPassword,
      phone: this.phone || undefined
    };

    this.authService.register(registerRequest).subscribe({
      next: (response) => {
        this.loading = false;
        this.successMessage = 'Inscription réussie! Redirection...';
        setTimeout(() => {
          this.router.navigate(['viewBooks']);
        }, 1500);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Une erreur est survenue lors de l\'inscription';
        console.error('Register error:', err);
      }
    });
  }

  onLogin(): void {
    this.router.navigate(['/connection']);
  }
}

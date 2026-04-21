import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoanService } from '../../services/loan.service';
import { AuthService } from '../../services/auth-service';
import { LoanResponse } from '../../Interface/loan';

@Component({
  selector: 'app-loan-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './loan-view.html',
  styleUrl: './loan-view.css'
})
export class LoanView implements OnInit {

  loans: LoanResponse[] = [];
  erreur: string = '';
  succes: string = '';
  userId: number = 0;

  constructor(
    private loanService: LoanService,
    private authService: AuthService,
  private cdr : ChangeDetectorRef
  ) {}

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    this.userId = user?.userId;
    this.chargerEmprunts();
  }

  chargerEmprunts() {
    this.loanService.getLoansByUser(this.userId).subscribe({
      next: (data) => {
        this.loans = data;
          this.cdr.detectChanges();
      },
      error: () => this.erreur = 'Impossible de charger vos emprunts.'
    });
  }

  retournerLivre(id: number) {
    this.loanService.returnBook(id).subscribe({
      next: () => {
        this.succes = 'Livre retourné avec succès !';
        this.erreur = '';
        this.chargerEmprunts();
      },
      error: () => this.erreur = 'Erreur lors du retour du livre.'
    });
  }
}

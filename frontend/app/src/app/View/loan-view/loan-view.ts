import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoanService } from '../../services/loan-service';
import { AuthService } from '../../services/auth-service';
import { BookService } from '../../services/book-service';
import { LoanResponse } from '../../Interface/loan';
import {Book} from '../../Interface/book';

interface LoanWithBook extends LoanResponse {
  bookTitle?: string;
}

@Component({
  selector: 'app-loan-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './loan-view.html',
  styleUrl: './loan-view.css'
})
export class LoanView implements OnInit {
  loans: LoanWithBook[] = [];
  erreur: string = '';
  succes: string = '';
  userId: number = 0;

  constructor(
    private loanService: LoanService,
    private authService: AuthService,
    private bookService: BookService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    if (!user || !user.userId) {
      this.erreur = "Utilisateur non connecté";
      return;
    }
    this.userId = user.userId;
    this.chargerEmprunts();
  }

  chargerEmprunts() {
    console.log("Tentative de chargement des emprunts pour l'utilisateur ID:", this.userId);

    this.erreur = '';

    this.loanService.getLoansByUser(this.userId).subscribe({
      next: (loansData: LoanResponse[]) => {
        console.log("Données d'emprunts reçues :", loansData);

        if (!loansData || loansData.length === 0) {
          this.loans = [];
          this.cdr.detectChanges();
          return;
        }

        this.loans = loansData.map(l => ({
          ...l,
          bookTitle: 'Chargement du titre...'
        }));
        this.loans.forEach((loan, index) => {

          console.log(`Emprunt n°${index} : Récupération du livre via l'ID : ${loan.bookId}`);

          this.bookService.getBookById(loan.bookId).subscribe({
            next: (book: Book) => {
              console.log(`Succès pour le livre ${loan.bookId} :`, book.title);
              loan.bookTitle = book.title;
              this.cdr.detectChanges();
            },
            error: (err) => {

              console.error(`Échec de récupération du livre (ID: ${loan.bookId}) :`, err);

              loan.bookTitle = "Détails indisponibles (Erreur Serveur)";
              this.cdr.detectChanges();
            }
          });
        });
      },
      error: (err) => {
        console.error("Erreur critique lors de la récupération des emprunts :", err);
        this.erreur = "Impossible de charger la liste des emprunts. Vérifiez votre connexion au serveur.";
        this.cdr.detectChanges();
      }
    });
  }

  retournerLivre(id: number) {
    this.loanService.returnBook(id).subscribe({
      next: () => {
        this.succes = 'Livre retourné avec succès !';
        this.erreur = '';
        this.chargerEmprunts();
      },
      error: () => {
        this.erreur = 'Erreur lors du retour du livre.';
        this.succes = '';
      }
    });
  }
}

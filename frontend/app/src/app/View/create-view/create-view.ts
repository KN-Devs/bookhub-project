import {ChangeDetectorRef, Component} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf} from '@angular/common';
import {BookService} from '../../services/book-service';
import {Router} from '@angular/router';
import {CategoriesService} from '../../services/categories-service';

@Component({
  selector: 'app-create-view',
  imports: [
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './create-view.html',
  styleUrl: './create-view.css',
})
export class CreateView {
  bookForm: FormGroup;

  categories: any[] = [];


  constructor(
    private fb: FormBuilder,
    private bookService: BookService,
    private router: Router,
    private categoriesService : CategoriesService,
    private cdr : ChangeDetectorRef

  ) {
    this.bookForm = this.fb.group({
      title: ['', Validators.required],
      author: ['', Validators.required],
      isbn: ['', Validators.required],
      description: [''],
      coverImage: [''],
      totalCopies: [1, [Validators.required, Validators.min(0)]],
      availableCopies: [1, [Validators.required, Validators.min(0)]],
      categoryId: [1, Validators.required]
    });
  }


  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories() {
    this.categoriesService.getAllCategories().subscribe({
      next: (data) => {
        this.categories = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error("Erreur chargement catégories", err);
      }
    });
  }

  submit() {
    if (this.bookForm.invalid) {
      this.bookForm.markAllAsTouched();
      return;
    }

    this.bookService.createBook(this.bookForm.value).subscribe({
      next: () => {
        alert('Livre ajouté avec succès 👍');
        this.router.navigate(['/viewBooks']);
      },
      error: (err) => {
        console.error(err);
        alert('Erreur lors de la création');
      }
    });
  }


}

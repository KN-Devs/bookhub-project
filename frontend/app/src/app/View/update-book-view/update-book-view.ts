import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {BookService} from '../../services/book-service';
import {Book} from '../../Interface/book';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {CategoriesService} from '../../services/categories-service';

@Component({
  selector: 'app-update-book-view',
  imports: [
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './update-book-view.html',
  styleUrl: './update-book-view.css',
})
export class UpdateBookView implements OnInit {

  categories: any[] = [];
  bookForm: FormGroup;
  isbn: string = "";
  book!: Book;

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private cdr : ChangeDetectorRef,
    private fb: FormBuilder,
    private router: Router,
    private categoriesService : CategoriesService
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
    this.loadBook();
  }

  loadCategories() {
    this.categoriesService.getAllCategories().subscribe({
      next: (data) => {
        this.categories = data;
        this.cdr.detectChanges();
      }
    });
  }

  loadBook() {
    const isbn = this.route.snapshot.paramMap.get('isbn');

    if (isbn) {
      this.bookService.getBookByIsbn(isbn).subscribe({
        next: (data) => {
          this.book = data;

          this.bookForm.patchValue({
            title: data.title,
            author: data.author,
            isbn: data.isbn,
            description: data.description,
            coverImage: data.coverImage,
            totalCopies: data.totalCopies,
            availableCopies: data.availableCopies,
            categoryId: data.category
          });
          this.cdr.detectChanges();
        }
      });
    }
  }

  submit() {
    if (this.bookForm.invalid) {
      this.bookForm.markAllAsTouched();
      return;
    }

    this.bookService.updateBook(this.book.isbn, this.bookForm.value).subscribe({
      next: () => {
        alert('Livre modifié avec succès 👍');
        this.router.navigate(['/viewBooks']);
      },
      error: (err) => {
        console.error(err);
        alert('Erreur lors de la mise à jour');
      }
    });
  }



}

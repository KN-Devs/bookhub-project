package fr.bookhub.controller;

import fr.bookhub.bo.User;
import fr.bookhub.dto.BookRequest;
import fr.bookhub.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import fr.bookhub.bo.Book;
import fr.bookhub.dal.BooksRepository;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BooksRepository bookRepository;
    private final BookService bookService;

    public BookController(BooksRepository bookRepository, BookService bookService ){
        this.bookRepository = bookRepository;
        this.bookService = bookService;

    }

    // Trouver tout les livres
    @GetMapping
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    // Trouver un livre par l'ISBN
    @GetMapping("/{isbn}")
    public ResponseEntity<Book> findByIsbn(@PathVariable String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    // Trouver un livre par l'id
    @GetMapping("/id/{id}")
    public ResponseEntity<Book> findByIsbn(@PathVariable int id) {
        Book book = bookRepository.findById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    // CREATE
    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<Book> createBook(@RequestBody BookRequest dto) {
        Book created = bookService.createBook(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // UPDATE
    @PutMapping("/{isbn}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<Book> updateBook(
            @PathVariable String isbn,
            @RequestBody BookRequest dto) {

        Book updated = bookService.updateBook(isbn, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{isbn}/borrow")
    public ResponseEntity<?> borrowBook(
            @PathVariable String isbn,
            @AuthenticationPrincipal User currentUser) {
        bookService.borrowBook(isbn, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{isbn}/reserve")
    public ResponseEntity<?> reserveBook(
            @PathVariable String isbn,
            @AuthenticationPrincipal User currentUser) {
        bookService.reserveBook(isbn, currentUser.getId());
        return ResponseEntity.ok().build();
    }

}
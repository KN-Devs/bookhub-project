package fr.bookhub.service;

import fr.bookhub.bo.Book;
import fr.bookhub.bo.Categories;
import fr.bookhub.dal.BooksRepository;
import fr.bookhub.dal.CategoriesRepository;
import fr.bookhub.dto.BookRequest;
import fr.bookhub.exception.BadRequestException;
import fr.bookhub.exception.BookNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class BookService {

    private final BooksRepository bookRepository;
    private final CategoriesRepository categoriesRepository;

    public BookService(BooksRepository bookRepository,
                       CategoriesRepository categoriesRepository) {
        this.bookRepository = bookRepository;
        this.categoriesRepository = categoriesRepository;
    }

    // CREATE
    public Book createBook(BookRequest dto) {

        // validation champs obligatoires
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new BadRequestException("Titre obligatoire");
        }

        if (dto.getAuthor() == null || dto.getAuthor().isBlank()) {
            throw new BadRequestException("Auteur obligatoire");
        }

        if (dto.getIsbn() == null || dto.getIsbn().isBlank()) {
            throw new BadRequestException("ISBN obligatoire");
        }

        // ISBN unique
        Book existing = bookRepository.findByIsbn(dto.getIsbn());
        if (existing != null) {
            throw new BadRequestException("l'ISBN existe déja");
        }

        // catégorie obligatoire
        Categories category = categoriesRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Catégorie inconnue"));

        // copies logiques
        if (dto.getTotalCopies() < 0 || dto.getAvailableCopies() < 0) {
            throw new BadRequestException("Nombre de copie positif");
        }

        if (dto.getAvailableCopies() > dto.getTotalCopies()) {
            throw new BadRequestException("Le nombre de copie ne peux etre supérieurs au maximum de copie");
        }

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setDescription(dto.getDescription());
        book.setCoverImage(dto.getCoverImage());
        book.setAvailable(dto.isAvailable());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getAvailableCopies());
        book.setAverageRating(dto.getAverageRating());
        book.setCategory(category);
        book.setCreatedAt(LocalDateTime.now());

        return bookRepository.save(book);
    }

    // 🔵 UPDATE
    public Book updateBook(String isbn, BookRequest dto) {

        Book book = bookRepository.findByIsbn(isbn);
        if(book == null){
            throw new BookNotFoundException("Livre inconnue");
        }

        // validation simple
        if (dto.getTitle() != null && dto.getTitle().isBlank()) {
            throw new BadRequestException("Title cannot be empty");
        }

        // update champ par champ (safe)
        if (dto.getTitle() != null) book.setTitle(dto.getTitle());
        if (dto.getAuthor() != null) book.setAuthor(dto.getAuthor());
        if (dto.getDescription() != null) book.setDescription(dto.getDescription());
        if (dto.getCoverImage() != null) book.setCoverImage(dto.getCoverImage());

        if (dto.getTotalCopies() != 0)
            book.setTotalCopies(dto.getTotalCopies());

        if (dto.getAvailableCopies() != 0)
            book.setAvailableCopies(dto.getAvailableCopies());

        if (dto.getCategoryId() != null) {
            Categories category = categoriesRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new BadRequestException("Category not found"));
            book.setCategory(category);
        }

        return bookRepository.save(book);
    }
}
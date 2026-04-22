package fr.bookhub.service;

import fr.bookhub.bo.Book;
import fr.bookhub.bo.Categories;
import fr.bookhub.dal.BooksRepository;
import fr.bookhub.dal.CategoriesRepository;
import fr.bookhub.dto.BookRequest;
import fr.bookhub.dto.BookResponse;
import fr.bookhub.exception.BadRequestException;
import fr.bookhub.exception.BookNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BooksRepository bookRepository;
    private final CategoriesRepository categoriesRepository;

    public BookServiceImpl(BooksRepository bookRepository,
                           CategoriesRepository categoriesRepository) {
        this.bookRepository = bookRepository;
        this.categoriesRepository = categoriesRepository;
    }

    @Override
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponse getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) throw new BookNotFoundException("Livre introuvable");
        return toDTO(book);
    }

    @Override
    public BookResponse getBookById(Integer id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Livre introuvable"));
        return toDTO(book);
    }

    @Override
    public Book createBook(BookRequest dto) {
        // Validations obligatoires
        if (dto.getTitle() == null || dto.getTitle().isBlank())
            throw new BadRequestException("Titre obligatoire");

        if (dto.getAuthor() == null || dto.getAuthor().isBlank())
            throw new BadRequestException("Auteur obligatoire");

        if (dto.getIsbn() == null || dto.getIsbn().isBlank())
            throw new BadRequestException("ISBN obligatoire");

        // Vérification de l'unicité de l'ISBN
        Book existing = bookRepository.findByIsbn(dto.getIsbn());
        if (existing != null)
            throw new BadRequestException("L'ISBN existe déjà");

        // Vérification de la catégorie
        Categories category = categoriesRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Catégorie inconnue"));

        // Création de l'entité
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setDescription(dto.getDescription());
        book.setCoverImage(dto.getCoverImage());

        // Gestion des exemplaires
        int total = (dto.getTotalCopies() != null) ? dto.getTotalCopies() : 1;
        int available = (dto.getAvailableCopies() != null) ? dto.getAvailableCopies() : 1;

        book.setTotalCopies(total);
        book.setAvailableCopies(available);
        book.setAvailable(available > 0);

        book.setCategory(category);
        book.setCreatedAt(LocalDateTime.now());

        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(String isbn, BookRequest dto) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) throw new BookNotFoundException("Livre inconnu");

        // Mise à jour partielle des champs
        if (dto.getTitle() != null) {
            if (dto.getTitle().isBlank()) throw new BadRequestException("Titre vide");
            book.setTitle(dto.getTitle());
        }

        if (dto.getAuthor() != null) book.setAuthor(dto.getAuthor());
        if (dto.getDescription() != null) book.setDescription(dto.getDescription());
        if (dto.getCoverImage() != null) book.setCoverImage(dto.getCoverImage());

        if (dto.getTotalCopies() != null) book.setTotalCopies(dto.getTotalCopies());

        if (dto.getAvailableCopies() != null) {
            book.setAvailableCopies(dto.getAvailableCopies());
            book.setAvailable(dto.getAvailableCopies() > 0);
        }

        if (dto.getCategoryId() != null) {
            Categories category = categoriesRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new BadRequestException("Catégorie introuvable"));
            book.setCategory(category);
        }

        return bookRepository.save(book);
    }

    private BookResponse toDTO(Book book) {
        BookResponse dto = new BookResponse();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setDescription(book.getDescription());
        dto.setCoverImage(book.getCoverImage());
        dto.setAvailable(book.isAvailable());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setAvailableCopies(book.getAvailableCopies());

        // Mapping de la catégorie
        if (book.getCategory() != null) {
            // Note: utilisez getName() ou getNom() selon votre classe Categories
            dto.setCategoryName(book.getCategory().getName());
        }

        // Récupération des statistiques d'avis
        Double avg = bookRepository.findAverageRatingByBookId(book.getId());
        Integer count = bookRepository.findReviewCountByBookId(book.getId());

        dto.setAverageRating(avg != null ? avg : 0.0);
        dto.setReviewCount(count != null ? count : 0);

        return dto;
    }
}
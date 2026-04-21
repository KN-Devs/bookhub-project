package fr.bookhub.service;

import fr.bookhub.bo.Book;
import fr.bookhub.bo.Categories;
import fr.bookhub.bo.Loans;
import fr.bookhub.bo.Reservations;
import fr.bookhub.dal.BooksRepository;
import fr.bookhub.dal.CategoriesRepository;
import fr.bookhub.dal.LoansRepository;
import fr.bookhub.dal.ReservationsRepository;
import fr.bookhub.dto.BookRequest;
import fr.bookhub.exception.BadRequestException;
import fr.bookhub.exception.BookNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@Service
public class BookService {

    private final BooksRepository bookRepository;
    private final CategoriesRepository categoriesRepository;
    private final LoansRepository loansRepository;
    private final ReservationsRepository reservationsRepository;

    public BookService(BooksRepository bookRepository,
                       CategoriesRepository categoriesRepository,
                       LoansRepository loansRepository,
                       ReservationsRepository reservationsRepository) {
        this.bookRepository = bookRepository;
        this.categoriesRepository = categoriesRepository;
        this.loansRepository = loansRepository;
        this.reservationsRepository = reservationsRepository;
    }

    // ─── CREATE ────────────────────────────────────────────────────────
    public Book createBook(BookRequest dto) {

        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new BadRequestException("Titre obligatoire");
        }
        if (dto.getAuthor() == null || dto.getAuthor().isBlank()) {
            throw new BadRequestException("Auteur obligatoire");
        }
        if (dto.getIsbn() == null || dto.getIsbn().isBlank()) {
            throw new BadRequestException("ISBN obligatoire");
        }

        Book existing = bookRepository.findByIsbn(dto.getIsbn());
        if (existing != null) {
            throw new BadRequestException("L'ISBN existe déjà");
        }

        Categories category = categoriesRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Catégorie inconnue"));

        if (dto.getTotalCopies() < 0 || dto.getAvailableCopies() < 0) {
            throw new BadRequestException("Nombre de copie doit être positif");
        }
        if (dto.getAvailableCopies() > dto.getTotalCopies()) {
            throw new BadRequestException("Le nombre de copies disponibles ne peut pas dépasser le total");
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

    // ─── UPDATE ────────────────────────────────────────────────────────
    public Book updateBook(String isbn, BookRequest dto) {

        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            throw new BookNotFoundException("Livre inconnu");
        }

        if (dto.getTitle() != null && dto.getTitle().isBlank()) {
            throw new BadRequestException("Le titre ne peut pas être vide");
        }

        if (dto.getTitle() != null)       book.setTitle(dto.getTitle());
        if (dto.getAuthor() != null)      book.setAuthor(dto.getAuthor());
        if (dto.getDescription() != null) book.setDescription(dto.getDescription());
        if (dto.getCoverImage() != null)  book.setCoverImage(dto.getCoverImage());
        if (dto.getTotalCopies() != 0)    book.setTotalCopies(dto.getTotalCopies());
        if (dto.getAvailableCopies() != 0) book.setAvailableCopies(dto.getAvailableCopies());

        if (dto.getCategoryId() != null) {
            Categories category = categoriesRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new BadRequestException("Catégorie introuvable"));
            book.setCategory(category);
        }

        return bookRepository.save(book);
    }

    // ─── BORROW ────────────────────────────────────────────────────────
    public void borrowBook(String isbn, int userId) {

        // 1. Trouver le livre
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            throw new BookNotFoundException("Livre introuvable");
        }

        // 2. Vérifier la disponibilité
        if (book.getAvailableCopies() <= 0) {
            throw new BadRequestException("Livre indisponible");
        }

        // 3. Vérifier la limite de 3 emprunts actifs
        long activeLoans = loansRepository.countByUserIdAndStatus(userId, "ACTIVE");
        if (activeLoans >= 3) {
            throw new BadRequestException("Limite de 3 emprunts simultanés atteinte");
        }

        // 4. Vérifier qu'il n'y a pas de retard en cours
        boolean hasOverdue = loansRepository.existsByUserIdAndStatus(userId, "OVERDUE");
        if (hasOverdue) {
            throw new BadRequestException("Vous avez un emprunt en retard, impossible d'emprunter");
        }

        // 5. Créer l'emprunt (durée fixe 14 jours)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 14);

        Loans loan = new Loans();
        loan.setUserId(userId);
        loan.setBookId(book.getId());
        loan.setLoanDate(new Date());
        loan.setDueDate(cal.getTime());
        loan.setStatus("ACTIVE");
        loansRepository.save(loan);

        // 6. Décrémenter les copies disponibles
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        if (book.getAvailableCopies() == 0) {
            book.setAvailable(false);
        }
        bookRepository.save(book);
    }

    // ─── RESERVE ───────────────────────────────────────────────────────
    public void reserveBook(String isbn, int userId) {

        // 1. Trouver le livre
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            throw new BookNotFoundException("Livre introuvable");
        }

        // 2. Réserver seulement si le livre est indisponible
        if (book.getAvailableCopies() > 0) {
            throw new BadRequestException("Le livre est disponible, empruntez-le directement");
        }

        // 3. Vérifier que l'user n'a pas déjà réservé ce livre
        boolean alreadyReserved = reservationsRepository
                .existsByUserIdAndBookIdAndStatus(userId, book.getId(), "PENDING");
        if (alreadyReserved) {
            throw new BadRequestException("Vous avez déjà une réservation en cours pour ce livre");
        }

        // 4. Calculer le rang dans la file d'attente
        int rank = reservationsRepository.countByBookIdAndStatus(book.getId(), "PENDING") + 1;

        // 5. Créer la réservation
        Reservations reservation = new Reservations();
        reservation.setUserId(userId);
        reservation.setBookId(book.getId());
        reservation.setReservationDate(new Date());
        reservation.setRankInLine(rank);
        reservation.setStatus("PENDING");
        reservationsRepository.save(reservation);
    }
}
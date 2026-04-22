package fr.bookhub.service;

import fr.bookhub.bo.Book;
import fr.bookhub.bo.Loans;
import fr.bookhub.dal.BooksRepository;
import fr.bookhub.dal.LoansRepository;
import fr.bookhub.dto.LoansRequestDTO;
import fr.bookhub.dto.LoansResponseDTO;
import fr.bookhub.exception.*;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoansServiceImpl implements LoansService {

    private final LoansRepository loansRepository;
    private final BooksRepository bookRepository;

    public LoansServiceImpl(LoansRepository loansRepository,
                            BooksRepository bookRepository) {
        this.loansRepository = loansRepository;
        this.bookRepository = bookRepository;
    }

    // ─── Mapper BO → DTO ───────────────────────────────────────────────
    private LoansResponseDTO toDTO(Loans loan) {
        LoansResponseDTO dto = new LoansResponseDTO();
        dto.setId(loan.getId());
        dto.setUserId(loan.getUserId());
        dto.setBookId(loan.getBookId());
        dto.setLoanDate(loan.getLoanDate());
        dto.setDueDate(loan.getDueDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setStatus(loan.getStatus());
        dto.setBookTitle(bookRepository.findById(loan.getBookId()).getTitle());

        return dto;
    }

    // ─── CREATE ────────────────────────────────────────────────────────
    @Override
    public LoansResponseDTO createLoan(LoansRequestDTO dto) {

        // RG-LOAN-01 : max 3 emprunts simultanés
        long activeLoans = loansRepository
                .countByUserIdAndStatus(dto.getUserId(), "ACTIVE");
        if (activeLoans >= 3) {
            throw new LoansLimitExceededException(dto.getUserId());
        }

        // RG-LOAN-03 : bloqué si retard en cours
        boolean hasOverdue = loansRepository
                .existsByUserIdAndStatus(dto.getUserId(), "OVERDUE");
        if (hasOverdue) {
            throw new UserBlockedByOverdueException(dto.getUserId());
        }

        // RG-LOAN-04 : livre déjà emprunté en cours ?
        boolean alreadyActive = loansRepository
                .existsByBookIdAndStatus(dto.getBookId(), "ACTIVE");
        if (alreadyActive) {
            throw new BookAlreadyBorrowedException(dto.getBookId());
        }

        Book book = bookRepository.findById(dto.getBookId());
        if (book == null) {
            throw new BookNotFoundException("livre non trouvé");
        }

        if (book.getAvailableCopies() <= 0) {
            throw new BookAlreadyBorrowedException(dto.getBookId());
        }
        // réduire stock
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // RG-LOAN-02 : durée fixe = 14 jours
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 14);

        Loans loan = new Loans();
        loan.setUserId(dto.getUserId());
        loan.setBookId(dto.getBookId());
        loan.setLoanDate(new Date());
        loan.setDueDate(cal.getTime());
        loan.setStatus("ACTIVE");

        return toDTO(loansRepository.save(loan));
    }

    // ─── READ ──────────────────────────────────────────────────────────
    @Override
    public LoansResponseDTO getLoanById(int id) {
        return toDTO(loansRepository.findById(id)
                .orElseThrow(() -> new LoansNotFoundException(id)));
    }

    @Override
    public List<LoansResponseDTO> getAllLoans() {
        return loansRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<LoansResponseDTO> getLoansByUser(int userId) {
        return loansRepository.findByUserId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<LoansResponseDTO> getLoansByStatus(String status) {
        return loansRepository.findByStatus(status)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ─── RETURN ────────────────────────────────────────────────────────
    @Override
    public LoansResponseDTO returnBook(int id) {
        Loans loan = loansRepository.findById(id)
                .orElseThrow(() -> new LoansNotFoundException(id));

        Date now = new Date();
        loan.setReturnDate(now);
        loan.setStatus(now.after(loan.getDueDate()) ? "OVERDUE" : "RETURNED");

        // récupérer le livre
        Book book = bookRepository.findById(loan.getBookId());
        if (book == null) {
            throw new BookNotFoundException("livre non trouvé");
        }
        // remettre en stock
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return toDTO(loansRepository.save(loan));
    }

    // ─── DELETE ────────────────────────────────────────────────────────
    @Override
    public void deleteLoan(int id) {
        if (!loansRepository.existsById(id)) {
            throw new LoansNotFoundException(id);
        }
        loansRepository.deleteById(id);
    }
}
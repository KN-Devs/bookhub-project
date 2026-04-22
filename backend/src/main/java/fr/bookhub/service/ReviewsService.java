package fr.bookhub.service;

import fr.bookhub.bo.Book;
import fr.bookhub.bo.Reviews;
import fr.bookhub.bo.User;
import fr.bookhub.dal.BooksRepository;
import fr.bookhub.dal.ReviewsRepository;
import fr.bookhub.dal.UserRepository;
import fr.bookhub.dto.ReviewsRequestDTO;
import fr.bookhub.dto.ReviewsResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class ReviewsService {

    private final ReviewsRepository reviewRepository;
    private final BooksRepository bookRepository;
    private final UserRepository userRepository;

    public ReviewsService(ReviewsRepository reviewRepo, BooksRepository bookRepo, UserRepository userRepo) {
        this.reviewRepository = reviewRepo;
        this.bookRepository = bookRepo;
        this.userRepository = userRepo;
    }

    // 1. Est-ce que l'utilisateur a déjà voté ?
    public boolean hasUserReviewed(Integer bookId, Integer userId) {
        return reviewRepository.existsByBookIdAndUserId(bookId, userId);
    }

    // 2. Récupérer les avis (Version simple sans Stream)
    public List<ReviewsResponseDTO> getReviewsByBook(Integer bookId) {
        List<Reviews> reviewsList = reviewRepository.findByBookId(bookId);
        List<ReviewsResponseDTO> dtos = new ArrayList<>();

        for (Reviews r : reviewsList) {
            dtos.add(toDTO(r)); // On transforme chaque avis un par un
        }
        return dtos;
    }

    // 3. Créer un avis
    @Transactional
    public ReviewsResponseDTO createReview(ReviewsRequestDTO dto, Integer userId) {
        // Sécurité : on vérifie si l'avis existe déjà
        if (hasUserReviewed(dto.getBookId(), userId)) {
            throw new IllegalStateException("Tu as déjà noté ce livre !");
        }

        // On cherche le livre et l'user
        Book book = bookRepository.findById(dto.getBookId()).get();
        User user = userRepository.findById(userId).get();

        // On crée l'objet Review
        Reviews review = new Reviews();
        review.setUser(user);
        review.setBook(book);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setModerated(true);

        // On sauvegarde
        Reviews saved = reviewRepository.save(review);

        // On met à jour les stats du livre
        updateBookStats(book);

        return toDTO(saved);
    }

    // 4. Calculer la moyenne (Version simple avec une boucle)
    private void updateBookStats(Book book) {
        List<Reviews> allReviews = reviewRepository.findByBookId(book.getId());

        int totalNotes = 0;
        for (Reviews r : allReviews) {
            totalNotes += r.getRating();
        }

        double moyenne = (double) totalNotes / allReviews.size();

        book.setReviewCount(allReviews.size());
        book.setAverageRating(moyenne);

        bookRepository.save(book);
    }

    // 5. La méthode de conversion (Le "Mapper")
    private ReviewsResponseDTO toDTO(Reviews r) {
        ReviewsResponseDTO dto = new ReviewsResponseDTO();
        dto.setId(r.getId());
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        dto.setModerated(r.isModerated());

        if (r.getUser() != null) {
            dto.setUsername(r.getUser().getUsername());
        }
        return dto;
    }
}
package fr.bookhub.service;

import fr.bookhub.bo.Reviews;
import fr.bookhub.dal.ReviewsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewsService {

    private final ReviewsRepository reviewsRepository;

    public ReviewsService(ReviewsRepository reviewsRepository) {
        this.reviewsRepository = reviewsRepository;
    }

    // Récupérer tous les avis d'un livre
    public List<Reviews> getReviewsByBookId(int bookId) {
        return reviewsRepository.findReviewsByBookId(bookId);
    }

    // Ajouter un avis
    public Reviews addReview(Reviews review) {
        return reviewsRepository.save(review);
    }

    // Note moyenne d'un livre
    public double getAverageRating(int bookId) {
        List<Reviews> reviews = reviewsRepository.findReviewsByBookId(bookId);
        return reviews.stream()
                .mapToInt(Reviews::getRating)
                .average()
                .orElse(0.0);
    }
}
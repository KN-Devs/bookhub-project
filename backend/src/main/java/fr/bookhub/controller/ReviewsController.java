package fr.bookhub.controller;

import fr.bookhub.service.ReviewsService;
import fr.bookhub.bo.Book;
import fr.bookhub.bo.Reviews;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:4200")
public class ReviewsController {

    private final ReviewsService reviewsService;

    public ReviewsController(ReviewsService reviewsService) {
        this.reviewsService = reviewsService;
    }

    //  tous les avis du livre 1
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Reviews>> getReviews(@PathVariable int bookId) {
        return ResponseEntity.ok(reviewsService.getReviewsByBookId(bookId));
    }

    //  note moyenne
    @GetMapping("/book/{bookId}/average")
    public ResponseEntity<Map<String, Double>> getAverage(@PathVariable int bookId) {
        double avg = reviewsService.getAverageRating(bookId);
        return ResponseEntity.ok(Map.of("average", avg));
    }

    // poster un avis
    @PostMapping
    public ResponseEntity<Reviews> addReview(@RequestBody ReviewRequest request) {
        Reviews review = new Reviews();
        Book book = new Book();
        book.setId(request.bookId());
        review.setBook(book);
        review.setRating(request.rating());
        review.setComment(request.comment());
        review.setModerated(false);
        return ResponseEntity.ok(reviewsService.addReview(review));
    }

    // DTO pour la requête POST
    public record ReviewRequest(int bookId, int rating, String comment) {}
}
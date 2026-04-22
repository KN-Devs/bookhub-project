package fr.bookhub.controller;

import fr.bookhub.dto.ReviewsRequestDTO;
import fr.bookhub.dto.ReviewsResponseDTO;
import fr.bookhub.service.ReviewsService;
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

    // GET /api/reviews/book/{bookId}
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewsResponseDTO>> getByBook(@PathVariable Integer bookId) {
        return ResponseEntity.ok(reviewsService.getReviewsByBook(bookId));
    }

    // GET /api/reviews/check?bookId=1&userId=2
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> hasReviewed(
            @RequestParam Integer bookId,
            @RequestParam Integer userId) {
        boolean result = reviewsService.hasUserReviewed(bookId, userId);
        return ResponseEntity.ok(Map.of("hasReviewed", result));
    }

    // POST /api/reviews
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody ReviewsRequestDTO dto,
            @RequestParam Integer userId) {
        try {
            ReviewsResponseDTO created = reviewsService.createReview(dto, userId);
            return ResponseEntity.ok(created);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
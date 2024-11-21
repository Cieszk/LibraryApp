package pl.cieszk.libraryapp.features.reviews.application;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.cieszk.libraryapp.core.exceptions.custom.UnauthorizedAccessException;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.reviews.domain.Review;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@AllArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Review>> getAllReviewsByBook(@RequestBody Book book) {
        return ResponseEntity.ok(reviewService.getAllReviewsByBook(book));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewEntityById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Review> addReview(@RequestBody Review review) {
        return ResponseEntity.ok(reviewService.addReview(review));
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Review> updateReview(@RequestBody Review review) throws UnauthorizedAccessException {
        return ResponseEntity.ok(reviewService.updateReview(review));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) throws UnauthorizedAccessException {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}

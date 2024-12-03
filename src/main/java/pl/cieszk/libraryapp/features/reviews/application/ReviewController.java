package pl.cieszk.libraryapp.features.reviews.application;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.cieszk.libraryapp.core.exceptions.custom.UnauthorizedAccessException;
import pl.cieszk.libraryapp.features.books.application.dto.BookRequestDto;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.reviews.application.dto.ReviewRequestDto;
import pl.cieszk.libraryapp.features.reviews.application.dto.ReviewResponseDto;
import pl.cieszk.libraryapp.features.reviews.domain.Review;
import pl.cieszk.libraryapp.shared.dto.BookUserRequest;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@AllArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ReviewResponseDto>> getAllReviewsByBook(@RequestBody BookRequestDto bookRequestDto) {
        return ResponseEntity.ok(reviewService.getAllReviewsByBook(bookRequestDto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ReviewResponseDto> getReviewById(@RequestBody ReviewRequestDto reviewRequestDto) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewRequestDto));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ReviewResponseDto> addReview(@RequestBody ReviewRequestDto review) {
        return ResponseEntity.ok(reviewService.addReview(review));
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewResponseDto> updateReview(@RequestBody ReviewRequestDto review) throws UnauthorizedAccessException {
        return ResponseEntity.ok(reviewService.updateReview(review));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReview(@RequestBody ReviewRequestDto review) throws UnauthorizedAccessException {
        reviewService.deleteReview(review);
        return ResponseEntity.noContent().build();
    }
}

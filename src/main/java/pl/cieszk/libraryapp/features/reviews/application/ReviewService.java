package pl.cieszk.libraryapp.features.reviews.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.core.exceptions.custom.ReviewNotFoundException;
import pl.cieszk.libraryapp.core.exceptions.custom.UnauthorizedAccessException;
import pl.cieszk.libraryapp.features.auth.application.UserService;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.auth.domain.enums.UserRole;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.reviews.domain.Review;
import pl.cieszk.libraryapp.features.reviews.repository.ReviewRepository;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    public Review getReviewEntityById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
    }

    public List<Review> getAllReviewsByBook(Book book) {
        return reviewRepository.findByBook(book);
    }

    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    public Review updateReview(Review review) throws UnauthorizedAccessException {
        if (!reviewRepository.existsById(review.getReviewId())) {
            throw new ReviewNotFoundException("Review not found");
        }
        Long currentUserId = userService.getCurrentUser().getUserId();
        if (!Objects.equals(currentUserId, review.getUser().getUserId())) {
            throw new UnauthorizedAccessException("You are not allowed to update this review");
        }
        return reviewRepository.save(review);
    }

    public void deleteReview(Long id) throws UnauthorizedAccessException {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        User currentUser = userService.getCurrentUser();
        Long currentUserId = currentUser.getUserId();
        UserRole currentUserRole = currentUser.getRole();
        if (!Objects.equals(currentUserId, review.getUser().getUserId()) && !UserRole.ADMIN.equals(currentUserRole)) {
            throw new UnauthorizedAccessException("You are not allowed to delete this review");
        }
        reviewRepository.deleteById(id);
    }

}

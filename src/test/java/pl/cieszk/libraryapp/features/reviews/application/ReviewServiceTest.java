package pl.cieszk.libraryapp.features.reviews.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cieszk.libraryapp.core.exceptions.custom.ReviewNotFoundException;
import pl.cieszk.libraryapp.core.exceptions.custom.UnauthorizedAccessException;
import pl.cieszk.libraryapp.features.auth.application.UserService;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.auth.domain.enums.UserRole;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.reviews.domain.Review;
import pl.cieszk.libraryapp.features.reviews.repository.ReviewRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private UserService userService;

    private Review review;

    private User user;

    private Book book;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .userId(1L)
                .role(UserRole.USER)
                .build();

        book = Book.builder()
                .bookId(1L)
                .build();

        review = Review.builder()
                .reviewId(1L)
                .rating(5)
                .user(user)
                .book(book)
                .comment("Comment")
                .build();
    }

    @Test
    public void getReviewEntityById_ShouldReturnReview() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        // When
        Review result = reviewService.getReviewEntityById(1L);

        // Then
        assertEquals(review, result);
        verify(reviewRepository).findById(1L);
    }

    @Test
    public void getReviewEntityById_ShouldThrowReviewNotFoundException() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        assertThrows(ReviewNotFoundException.class, () -> reviewService.getReviewEntityById(1L));

        // Then
        verify(reviewRepository).findById(1L);
    }

    @Test
    public void getAllReviews_ShouldReturnListOfReviews() {
        // Given
        when(reviewRepository.findByBook(book)).thenReturn(List.of(review));

        // When
        var result = reviewService.getAllReviewsByBook(book);

        // Then
        assertEquals(List.of(review), result);
        verify(reviewRepository).findByBook(book);
    }

    @Test
    public void getAllReviews_ShouldReturnEmptyListWhenThereIsNoReviews() {
        // Given
        when(reviewRepository.findByBook(book)).thenReturn(List.of());

        // When
        var result = reviewService.getAllReviewsByBook(book);

        // Then
        assertEquals(List.of(), result);
        verify(reviewRepository).findByBook(book);
    }

    @Test
    public void addReview_ShouldReturnReview() {
        // Given
        when(reviewRepository.save(review)).thenReturn(review);

        // When
        var result = reviewService.addReview(review);

        // Then
        assertEquals(review, result);
        verify(reviewRepository).save(review);
    }

    @Test
    public void updateReview_ShouldReturnUpdatedReviewWhenAuthenticatedUserMatchReviewAuthor() throws UnauthorizedAccessException {
        // Given
        when(reviewRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.save(review)).thenReturn(review);
        when(userService.getCurrentUser()).thenReturn(review.getUser());

        // When
        var result = reviewService.updateReview(review);

        // Then
        assertEquals(review, result);
        verify(reviewRepository).save(review);
    }

    @Test
    public void updateReview_ShouldThrowUnauthorizedAccessExceptionWhenAuthenticatedUserDoesNotMatchReviewAuthor() {
        // Given
        when(reviewRepository.existsById(1L)).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(User.builder().userId(2L).build());

        // When
        assertThrows(UnauthorizedAccessException.class, () -> reviewService.updateReview(review));

        // Then
        verify(reviewRepository).existsById(1L);
    }

    @Test
    public void updateReview_ShouldThrowReviewNotFoundExceptionWhenReviewDoesNotExist() {
        // Given
        when(reviewRepository.existsById(1L)).thenReturn(false);

        // When
        assertThrows(ReviewNotFoundException.class, () -> reviewService.updateReview(review));

        // Then
        verify(reviewRepository).existsById(1L);
    }

    @Test
    public void deleteReview_ShouldDeleteReviewWhenAuthenticatedUserMatchReviewAuthor() throws UnauthorizedAccessException {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(userService.getCurrentUser()).thenReturn(user);

        // When
        reviewService.deleteReview(1L);

        // Then
        verify(reviewRepository).deleteById(1L);
    }

    @Test
    public void deleteReview_ShouldThrowUnauthorizedAccessExceptionWhenAuthenticatedUserDoesNotMatchReviewAuthor() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(userService.getCurrentUser()).thenReturn(User.builder().userId(2L).build());

        // When
        assertThrows(UnauthorizedAccessException.class, () -> reviewService.deleteReview(1L));

        // Then
        verify(reviewRepository).findById(1L);
    }

    @Test
    public void deleteReview_ShouldThrowReviewNotFoundExceptionWhenReviewDoesNotExist() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        assertThrows(ReviewNotFoundException.class, () -> reviewService.deleteReview(1L));

        // Then
        verify(reviewRepository).findById(1L);
    }

    @Test
    public void deleteReview_ShouldThrowUnauthorizedAccessExceptionWhenAuthenticatedUserIsNotAdmin() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(userService.getCurrentUser()).thenReturn(User.builder().userId(2L).role(UserRole.USER).build());

        // When
        assertThrows(UnauthorizedAccessException.class, () -> reviewService.deleteReview(1L));

        // Then
        verify(reviewRepository).findById(1L);
    }
}

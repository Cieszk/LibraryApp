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
import pl.cieszk.libraryapp.features.auth.application.dto.UserRequestDto;
import pl.cieszk.libraryapp.features.auth.application.dto.UserResponseDto;
import pl.cieszk.libraryapp.features.auth.application.mapper.UserMapper;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.auth.domain.enums.UserRole;
import pl.cieszk.libraryapp.features.books.application.dto.BookRequestDto;
import pl.cieszk.libraryapp.features.books.application.mapper.BookMapper;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.reviews.application.dto.ReviewRequestDto;
import pl.cieszk.libraryapp.features.reviews.application.dto.ReviewResponseDto;
import pl.cieszk.libraryapp.features.reviews.application.mapper.ReviewMapper;
import pl.cieszk.libraryapp.features.reviews.domain.Review;
import pl.cieszk.libraryapp.features.reviews.repository.ReviewRepository;
import pl.cieszk.libraryapp.shared.dto.BookUserRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private UserService userService;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private UserMapper userMapper;

    private Review review;

    private User user;

    private Book book;
    private ReviewResponseDto reviewResponseDto;
    private ReviewRequestDto reviewRequestDto;
    private BookRequestDto bookRequestDto;

    private BookUserRequest bookUserRequest;
    private UserResponseDto userResponseDto;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .userId(1L)
                .role(UserRole.USER)
                .build();

        book = Book.builder()
                .isbn("ISBN")
                .bookId(1L)
                .build();

        review = Review.builder()
                .reviewId(1L)
                .rating(5)
                .user(user)
                .book(book)
                .comment("Comment")
                .build();

        reviewResponseDto = ReviewResponseDto.builder()
                .build();
        bookUserRequest = BookUserRequest.builder()
                .book(new BookRequestDto())
                .user(new UserRequestDto())
                .userMapper(userMapper)
                .bookMapper(bookMapper)
                .build();
        reviewRequestDto = ReviewRequestDto.builder()
                .bookUserRequest(bookUserRequest)
                .build();
        bookRequestDto = BookRequestDto.builder()
                .isbn("ISBN")
                .build();
        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .build();
    }

    @Test
    public void getReviewEntityById_ShouldReturnReview() {
        // Given
        when(reviewRequestDto.getBookUserRequest().toBook()).thenReturn(book);
        when(reviewRequestDto.getBookUserRequest().toUser()).thenReturn(user);
        when(reviewRepository.findByBookAndUser(book, user)).thenReturn(Optional.of(review));
        when(reviewMapper.toResponseDto(review)).thenReturn(reviewResponseDto);

        // When
        ReviewResponseDto result = reviewService.getReviewById(reviewRequestDto);

        // Then
        assertEquals(reviewResponseDto, result);
        verify(reviewRepository).findByBookAndUser(book, user);
    }

    @Test
    public void getReviewEntityById_ShouldThrowReviewNotFoundException() {
        // Given
        when(reviewRequestDto.getBookUserRequest().toBook()).thenReturn(book);
        when(reviewRequestDto.getBookUserRequest().toUser()).thenReturn(user);
        when(reviewRepository.findByBookAndUser(book, user)).thenReturn(Optional.empty());

        // When
        assertThrows(ReviewNotFoundException.class, () -> reviewService.getReviewById(reviewRequestDto));

        // Then
        verify(reviewRepository).findByBookAndUser(book, user);
    }

    @Test
    public void getAllReviews_ShouldReturnListOfReviews() {
        // Given
        when(reviewRepository.findByBook_Isbn(book.getIsbn())).thenReturn(List.of(review));
        when(reviewMapper.toResponseDtos(List.of(review))).thenReturn(List.of(reviewResponseDto));

        // When
        List<ReviewResponseDto> result = reviewService.getAllReviewsByBook(bookRequestDto);

        // Then
        assertEquals(List.of(reviewResponseDto), result);
        verify(reviewRepository).findByBook_Isbn(book.getIsbn());
    }

    @Test
    public void getAllReviews_ShouldReturnEmptyListWhenThereIsNoReviews() {
        // Given
        when(reviewRepository.findByBook_Isbn(book.getIsbn())).thenReturn(List.of());
        when(reviewMapper.toResponseDtos(List.of())).thenReturn(List.of());

        // When
        List<ReviewResponseDto> result = reviewService.getAllReviewsByBook(bookRequestDto);

        // Then
        assertEquals(List.of(), result);
        verify(reviewRepository).findByBook_Isbn(book.getIsbn());
    }

    @Test
    public void addReview_ShouldReturnReview() {
        // Given
        when(reviewMapper.toEntity(reviewRequestDto)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toResponseDto(review)).thenReturn(reviewResponseDto);

        // When
        ReviewResponseDto result = reviewService.addReview(reviewRequestDto);

        // Then
        assertEquals(reviewResponseDto, result);
        verify(reviewRepository).save(review);
    }

    @Test
    public void updateReview_ShouldReturnUpdatedReviewWhenAuthenticatedUserMatchReviewAuthor() throws UnauthorizedAccessException {
        // Given
        when(reviewRequestDto.getBookUserRequest().toBook()).thenReturn(book);
        when(reviewRequestDto.getBookUserRequest().toUser()).thenReturn(user);
        when(reviewRepository.findByBookAndUser(book, user)).thenReturn(Optional.of(review));
        when(userService.getCurrentUser()).thenReturn(userResponseDto);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toResponseDto(review)).thenReturn(reviewResponseDto);

        // When
        ReviewResponseDto result = reviewService.updateReview(reviewRequestDto);

        // Then
        assertEquals(reviewResponseDto, result);
        verify(reviewRepository).save(review);
    }

    @Test
    public void updateReview_ShouldThrowUnauthorizedAccessExceptionWhenAuthenticatedUserDoesNotMatchReviewAuthor() {
        // Given
        when(reviewRequestDto.getBookUserRequest().toBook()).thenReturn(book);
        when(reviewRequestDto.getBookUserRequest().toUser()).thenReturn(user);
        when(reviewRepository.findByBookAndUser(book, user)).thenReturn(Optional.of(review));
        when(userService.getCurrentUser()).thenReturn(UserResponseDto.builder().id(2L).role(UserRole.USER).build());

        // When
        assertThrows(UnauthorizedAccessException.class, () -> reviewService.updateReview(reviewRequestDto));

        // Then
        verify(reviewRepository).findByBookAndUser(book, user);
        verify(userService).getCurrentUser();
        verify(reviewRepository, never()).save(review);
    }

    @Test
    public void updateReview_ShouldThrowReviewNotFoundExceptionWhenReviewDoesNotExist() {
        // Given
        when(reviewRequestDto.getBookUserRequest().toBook()).thenReturn(book);
        when(reviewRequestDto.getBookUserRequest().toUser()).thenReturn(user);
        when(reviewRepository.findByBookAndUser(book, user)).thenReturn(Optional.empty());

        // When
        assertThrows(ReviewNotFoundException.class, () -> reviewService.updateReview(reviewRequestDto));

        // Then
        verify(reviewRepository).findByBookAndUser(book, user);
        verify(userService, never()).getCurrentUser();
        verify(reviewRepository, never()).save(review);
    }

    @Test
    public void deleteReview_ShouldDeleteReviewWhenAuthenticatedUserMatchReviewAuthor() throws UnauthorizedAccessException {
        // Given
        when(reviewRequestDto.getBookUserRequest().toBook()).thenReturn(book);
        when(reviewRequestDto.getBookUserRequest().toUser()).thenReturn(user);
        when(reviewRepository.findByBookAndUser(book, user)).thenReturn(Optional.of(review));
        when(userService.getCurrentUser()).thenReturn(userResponseDto);


        // When
        reviewService.deleteReview(reviewRequestDto);

        // Then
        verify(reviewRepository).deleteById(review.getReviewId());
    }

    @Test
    public void deleteReview_ShouldThrowUnauthorizedAccessExceptionWhenAuthenticatedUserDoesNotMatchReviewAuthor() {
        // Given
        when(reviewRequestDto.getBookUserRequest().toBook()).thenReturn(book);
        when(reviewRequestDto.getBookUserRequest().toUser()).thenReturn(user);
        when(reviewRepository.findByBookAndUser(book, user)).thenReturn(Optional.of(review));
        when(userService.getCurrentUser()).thenReturn(UserResponseDto.builder().id(2L).role(UserRole.USER).build());

        // When
        assertThrows(UnauthorizedAccessException.class, () -> reviewService.deleteReview(reviewRequestDto));

        // Then
        verify(reviewRepository).findByBookAndUser(book, user);
        verify(reviewRepository, never()).deleteById(review.getReviewId());
    }

    @Test
    public void deleteReview_ShouldThrowReviewNotFoundExceptionWhenReviewDoesNotExist() {
        // Given
        when(reviewRequestDto.getBookUserRequest().toBook()).thenReturn(book);
        when(reviewRequestDto.getBookUserRequest().toUser()).thenReturn(user);
        when(reviewRepository.findByBookAndUser(book, user)).thenReturn(Optional.empty());

        // When
        assertThrows(ReviewNotFoundException.class, () -> reviewService.deleteReview(reviewRequestDto));

        // Then
        verify(reviewRepository).findByBookAndUser(book, user);
        verify(reviewRepository, never()).deleteById(review.getReviewId());
        verify(userService, never()).getCurrentUser();
    }

    @Test
    public void deleteReview_ShouldThrowUnauthorizedAccessExceptionWhenAuthenticatedUserIsNotAdmin() {
        // Given
        when(reviewRequestDto.getBookUserRequest().toBook()).thenReturn(book);
        when(reviewRequestDto.getBookUserRequest().toUser()).thenReturn(user);
        when(reviewRepository.findByBookAndUser(book, user)).thenReturn(Optional.of(review));
        when(userService.getCurrentUser()).thenReturn(UserResponseDto.builder().id(2L).role(UserRole.USER).build());

        // When
        assertThrows(UnauthorizedAccessException.class, () -> reviewService.deleteReview(reviewRequestDto));

        // Then
        verify(reviewRepository).findByBookAndUser(book, user);
        verify(reviewRepository, never()).deleteById(review.getReviewId());
    }
}

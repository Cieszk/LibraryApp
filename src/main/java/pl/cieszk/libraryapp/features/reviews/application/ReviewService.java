package pl.cieszk.libraryapp.features.reviews.application;

import lombok.AllArgsConstructor;
import org.mapstruct.control.MappingControl;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.core.exceptions.custom.ReviewNotFoundException;
import pl.cieszk.libraryapp.core.exceptions.custom.UnauthorizedAccessException;
import pl.cieszk.libraryapp.features.auth.application.UserService;
import pl.cieszk.libraryapp.features.auth.application.dto.UserResponseDto;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.auth.domain.enums.UserRole;
import pl.cieszk.libraryapp.features.books.application.dto.BookRequestDto;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.reviews.application.dto.ReviewRequestDto;
import pl.cieszk.libraryapp.features.reviews.application.dto.ReviewResponseDto;
import pl.cieszk.libraryapp.features.reviews.application.mapper.ReviewMapper;
import pl.cieszk.libraryapp.features.reviews.domain.Review;
import pl.cieszk.libraryapp.features.reviews.repository.ReviewRepository;
import pl.cieszk.libraryapp.shared.dto.BookUserRequest;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ReviewMapper reviewMapper;

    public ReviewResponseDto getReviewById(ReviewRequestDto reviewRequestDto) {
        User user = reviewRequestDto.getBookUserRequest().toUser();
        Book book = reviewRequestDto.getBookUserRequest().toBook();
        return reviewMapper.toResponseDto(reviewRepository.findByBookAndUser(book, user)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found")));
    }

    public List<ReviewResponseDto> getAllReviewsByBook(BookRequestDto bookRequestDto) {
        return reviewMapper.toResponseDtos(reviewRepository.findByBook_Isbn(bookRequestDto.getIsbn()));
    }

    public ReviewResponseDto addReview(ReviewRequestDto review) {
        return reviewMapper.toResponseDto(reviewRepository.save(reviewMapper.toEntity(review)));
    }

    public ReviewResponseDto updateReview(ReviewRequestDto reviewRequestDto) throws UnauthorizedAccessException {
        User user = reviewRequestDto.getBookUserRequest().toUser();
        Book book = reviewRequestDto.getBookUserRequest().toBook();
        Review review = reviewRepository.findByBookAndUser(book, user)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
        UserResponseDto userResponse = userService.getCurrentUser();
        if (!Objects.equals(userResponse.getId(), review.getUser().getUserId())) {
            throw new UnauthorizedAccessException("You are not allowed to update this review");
        }
        return reviewMapper.toResponseDto(reviewRepository.save(review));
    }

    public void deleteReview(ReviewRequestDto reviewRequestDto) throws UnauthorizedAccessException {
        User user = reviewRequestDto.getBookUserRequest().toUser();
        Book book = reviewRequestDto.getBookUserRequest().toBook();
        Review review = reviewRepository.findByBookAndUser(book, user)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        UserResponseDto currentUser = userService.getCurrentUser();
        Long currentUserId = currentUser.getId();
        UserRole currentUserRole = currentUser.getRole();
        if (!Objects.equals(currentUserId, review.getUser().getUserId()) && !UserRole.ADMIN.equals(currentUserRole)) {
            throw new UnauthorizedAccessException("You are not allowed to delete this review");
        }
        reviewRepository.deleteById(review.getReviewId());
    }

}

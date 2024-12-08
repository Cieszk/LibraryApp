package pl.cieszk.libraryapp.features.reviews.application.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import pl.cieszk.libraryapp.features.auth.application.mapper.UserMapper;
import pl.cieszk.libraryapp.features.books.application.mapper.BookMapper;
import pl.cieszk.libraryapp.features.reviews.application.dto.ReviewRequestDto;
import pl.cieszk.libraryapp.features.reviews.application.dto.ReviewResponseDto;
import pl.cieszk.libraryapp.features.reviews.domain.Review;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ReviewMapper {
    Review toEntity(ReviewRequestDto reviewRequestDto);
    Review toEntity(ReviewResponseDto reviewResponseDto);
    ReviewResponseDto toResponseDto(Review review);

    List<ReviewResponseDto> toResponseDtos(List<Review> reviews);
}

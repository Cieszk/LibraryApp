package pl.cieszk.libraryapp.features.reviews.application.mapper;

import org.mapstruct.Mapper;
import pl.cieszk.libraryapp.features.auth.application.mapper.UserMapper;
import pl.cieszk.libraryapp.features.books.application.mapper.BookMapper;
import pl.cieszk.libraryapp.features.reviews.application.dto.ReviewRequestDto;
import pl.cieszk.libraryapp.features.reviews.application.dto.ReviewResponseDto;
import pl.cieszk.libraryapp.features.reviews.domain.Review;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BookMapper.class})
public interface ReviewMapper {
    Review toEntity(ReviewRequestDto reviewRequestDto);
    Review toEntity(ReviewResponseDto reviewResponseDto);
    ReviewResponseDto toResponseDto(Review review);
}

package pl.cieszk.libraryapp.features.auth.application.mapper;

import org.mapstruct.Mapper;
import pl.cieszk.libraryapp.features.auth.application.dto.UserRequestDto;
import pl.cieszk.libraryapp.features.auth.application.dto.UserResponseDto;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.loans.application.mapper.BookLoanMapper;
import pl.cieszk.libraryapp.features.reservations.application.mapper.ReservationMapper;
import pl.cieszk.libraryapp.features.reviews.application.mapper.ReviewMapper;

@Mapper(componentModel = "spring", uses = {BookLoanMapper.class, ReviewMapper.class, ReservationMapper.class})
public interface UserMapper {
    User toEntity(UserRequestDto userRequestDto);
    User toEntity(UserResponseDto userResponseDto);
    UserResponseDto toResponseDto(User user);
}

package pl.cieszk.libraryapp.features.books.application.mapper;

import org.mapstruct.Mapper;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceRequestDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceResponseDto;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.loans.application.mapper.BookLoanMapper;
import pl.cieszk.libraryapp.features.reservations.application.mapper.ReservationMapper;

@Mapper(componentModel = "spring", uses = {BookMapper.class, ReservationMapper.class, BookLoanMapper.class})
public interface BookInstanceMapper {
    BookInstance toEntity(BookInstanceRequestDto bookInstanceRequestDto);
    BookInstance toEntity(BookInstanceResponseDto bookInstanceResponseDto);
    BookInstanceResponseDto toResponseDto(BookInstance bookInstance);
}

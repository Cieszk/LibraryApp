package pl.cieszk.libraryapp.features.books.application.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceRequestDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceResponseDto;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.loans.application.dto.BookLoanRequestDto;
import pl.cieszk.libraryapp.features.loans.application.dto.BookLoanResponseDto;
import pl.cieszk.libraryapp.features.loans.application.mapper.BookLoanMapper;
import pl.cieszk.libraryapp.features.loans.domain.BookLoan;
import pl.cieszk.libraryapp.features.reservations.application.mapper.ReservationMapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {BookMapper.class, ReservationMapper.class, BookLoanMapper.class})
public interface BookInstanceMapper {
    BookInstance toEntity(BookInstanceRequestDto bookInstanceRequestDto);
    BookInstance toEntity(BookInstanceResponseDto bookInstanceResponseDto);
    BookInstanceResponseDto toResponseDto(BookInstance bookInstance);
    Set<BookInstance> toEntities(Set<BookInstanceRequestDto> dtos);

    Set<BookInstanceRequestDto> toRequestDtos(Set<BookInstance> entities);
}

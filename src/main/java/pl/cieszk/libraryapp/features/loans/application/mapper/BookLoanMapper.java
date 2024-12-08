package pl.cieszk.libraryapp.features.loans.application.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import pl.cieszk.libraryapp.features.auth.application.mapper.UserMapper;
import pl.cieszk.libraryapp.features.books.application.mapper.BookInstanceMapper;
import pl.cieszk.libraryapp.features.loans.application.dto.BookLoanRequestDto;
import pl.cieszk.libraryapp.features.loans.application.dto.BookLoanResponseDto;
import pl.cieszk.libraryapp.features.loans.domain.BookLoan;

import java.util.Set;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookLoanMapper {

    BookLoan toEntity(BookLoanRequestDto bookLoanRequestDto);

    BookLoan toEntity(BookLoanResponseDto bookLoanResponseDto);

    BookLoanResponseDto toResponseDto(BookLoan bookLoan);

    Set<BookLoanResponseDto> toResponseDtos(Set<BookLoan> bookLoans);

    Set<BookLoan> toEntities(Set<BookLoanRequestDto> bookLoanRequestDtos);

}

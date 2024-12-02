package pl.cieszk.libraryapp.features.loans.application.mapper;

import org.mapstruct.Mapper;
import pl.cieszk.libraryapp.features.auth.application.mapper.UserMapper;
import pl.cieszk.libraryapp.features.books.application.mapper.BookInstanceMapper;
import pl.cieszk.libraryapp.features.loans.application.dto.BookLoanRequestDto;
import pl.cieszk.libraryapp.features.loans.application.dto.BookLoanResponseDto;
import pl.cieszk.libraryapp.features.loans.domain.BookLoan;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BookInstanceMapper.class})
public interface BookLoanMapper {
    BookLoan toEntity(BookLoanRequestDto bookLoanRequestDto);
    BookLoan toEntity(BookLoanResponseDto bookLoanResponseDto);
    BookLoanResponseDto toResponseDto(BookLoan bookLoan);
}

package pl.cieszk.libraryapp.features.loans.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.cieszk.libraryapp.features.auth.application.dto.UserRequestDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceRequestDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookLoanRequestDto {
    Long id;
    LocalDateTime loanDate;
    LocalDateTime returnDate;
    LocalDateTime dueDate;
    Double fineAmount;
    Integer renewCount;
    UserRequestDto user;
    BookInstanceRequestDto bookInstance;
}

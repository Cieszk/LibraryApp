package pl.cieszk.libraryapp.features.loans.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.cieszk.libraryapp.features.auth.application.dto.UserResponseDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceResponseDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookLoanResponseDto {
    LocalDateTime loanDate;
    LocalDateTime returnDate;
    LocalDateTime dueDate;
    Double fineAmount;
    Integer renewCount;
    UserResponseDto user;
    BookInstanceResponseDto bookInstance;
}

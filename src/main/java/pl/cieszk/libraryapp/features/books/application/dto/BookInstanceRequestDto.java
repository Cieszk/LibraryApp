package pl.cieszk.libraryapp.features.books.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.cieszk.libraryapp.features.books.domain.enums.BookStatus;
import pl.cieszk.libraryapp.features.loans.application.dto.BookLoanRequestDto;
import pl.cieszk.libraryapp.features.reservations.application.dto.ReservationRequestDto;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookInstanceRequestDto {
    private BookRequestDto book;
    private BookStatus bookStatus;
    private ReservationRequestDto reservation;
    private Set<BookLoanRequestDto> bookLoans;
}

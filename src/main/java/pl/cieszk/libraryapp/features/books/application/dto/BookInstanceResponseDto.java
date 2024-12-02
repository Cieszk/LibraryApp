package pl.cieszk.libraryapp.features.books.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.cieszk.libraryapp.features.loans.application.dto.BookLoanResponseDto;
import pl.cieszk.libraryapp.features.reservations.application.dto.ReservationResponseDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookInstanceResponseDto {
    BookResponseDto book;
    String bookStatus;
    ReservationResponseDto reservation;
    BookLoanResponseDto bookLoans;
}

package pl.cieszk.libraryapp.features.books.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.cieszk.libraryapp.features.loans.application.dto.BookLoanResponseDto;
import pl.cieszk.libraryapp.features.reservations.application.dto.ReservationResponseDto;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookInstanceResponseDto {
    private Long id;
    private BookResponseDto book;
    private String bookStatus;
    private ReservationResponseDto reservation;
    private Set<BookLoanResponseDto> bookLoans;
}

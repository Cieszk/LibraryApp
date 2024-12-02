package pl.cieszk.libraryapp.features.reservations.application.dto;

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
public class ReservationRequestDto {
    private LocalDateTime reservationDate;
    private LocalDateTime returnDate;

    private LocalDateTime dueDate;

    private UserRequestDto user;

    private BookInstanceRequestDto bookInstance;
}

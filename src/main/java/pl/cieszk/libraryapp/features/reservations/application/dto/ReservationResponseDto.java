package pl.cieszk.libraryapp.features.reservations.application.dto;

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
public class ReservationResponseDto {
    private Long id;
    private LocalDateTime reservationDate;
    private LocalDateTime returnDate;

    private LocalDateTime dueDate;

    private UserResponseDto user;

    private BookInstanceResponseDto bookInstance;
}

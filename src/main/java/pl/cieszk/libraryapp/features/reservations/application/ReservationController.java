package pl.cieszk.libraryapp.features.reservations.application;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.cieszk.libraryapp.core.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.core.exceptions.custom.NoReservationFoundException;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.reservations.domain.Reservation;
import pl.cieszk.libraryapp.shared.dto.BookUserRequest;

@RestController
@RequestMapping("/api/reservations")
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
@AllArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Reservation> addReservation(@RequestBody BookUserRequest bookUserRequest) throws BookNotAvailableException {
        Book book = bookUserRequest.getBook();
        User user = bookUserRequest.getUser();
        return ResponseEntity.ok(reservationService.createReservation(book, user));
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long reservationId) {
        reservationService.deleteReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<Reservation> extendReservation(@PathVariable Long reservationId) throws NoReservationFoundException {
        return ResponseEntity.ok(reservationService.extendReservation(reservationId));
    }
}

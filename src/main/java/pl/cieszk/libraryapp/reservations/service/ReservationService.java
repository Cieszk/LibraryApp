package pl.cieszk.libraryapp.reservations.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.auth.model.User;
import pl.cieszk.libraryapp.books.model.Book;
import pl.cieszk.libraryapp.books.model.BookInstance;
import pl.cieszk.libraryapp.books.service.BookInstanceService;
import pl.cieszk.libraryapp.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.reservations.model.Reservation;
import pl.cieszk.libraryapp.reservations.repository.ReservationRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ReservationService {

    private ReservationRepository reservationRepository;
    private BookInstanceService bookInstanceService;

    private final int MAX_RESERVATIONS = 3;

    public Reservation createReservation(BookInstance bookInstance, User user) throws BookNotAvailableException {
        if (!canUserReserveBook(user)) {
            throw new BookNotAvailableException("User has reached maximum number of reservations");
        }
        Optional<BookInstance> availableBookInstance = bookInstanceService.getAnyAvailable(bookInstance.getBook());
        if (availableBookInstance.isPresent()) {
            Reservation reservation = Reservation.builder()
                    .bookInstance(availableBookInstance.get())
                    .user(user)
                    .build();
            reservationRepository.save(reservation);
            return reservation;
        } else {
            throw new BookNotAvailableException("Book is not available for reservation");
        }
    }

    public Optional<Reservation> findReservationByUserAndBook(User user, Book book) {
        return reservationRepository.findByUserAndBookInstance_Book(user, book);
    }

    public void deleteReservation(Reservation reservation) {
        reservationRepository.delete(reservation);
    }

    private boolean canUserReserveBook(User user) {
        Optional<Reservation> reservation = reservationRepository.findByUser_UserId(user.getUserId());
        return reservation.isEmpty() || reservation.get().getUser().getReservations().size() < MAX_RESERVATIONS;
    }

    public int getMAX_RESERVATIONS() {
        return MAX_RESERVATIONS;
    }
}

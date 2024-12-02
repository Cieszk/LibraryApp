package pl.cieszk.libraryapp.features.reservations.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.core.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.core.exceptions.custom.NoReservationFoundException;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.books.application.BookInstanceService;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.reservations.domain.Reservation;
import pl.cieszk.libraryapp.features.reservations.repository.ReservationRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ReservationService {

    private ReservationRepository reservationRepository;
    private BookInstanceService bookInstanceService;

    private final int MAX_RESERVATIONS = 3;

    public Reservation createReservation(Book book, User user) throws BookNotAvailableException {
        if (!canUserReserveBook(user)) {
            throw new BookNotAvailableException("User has reached maximum number of reservations");
        }
        BookInstance availableBookInstance = bookInstanceService.getAnyAvailable(book);
        Reservation reservation = Reservation.builder()
                .bookInstance(availableBookInstance)
                .user(user)
                .build();
        reservationRepository.save(reservation);
        return reservation;
    }

    public Reservation findReservationByUserAndBook(User user, Book book) throws NoReservationFoundException {
        Optional<Reservation> reservation = reservationRepository.findByUserAndBookInstance_Book(user, book);
        if (reservation.isPresent()){
            return reservation.get();
        }
        throw new NoReservationFoundException("No reservation found for user and book");
    }

    public void deleteReservation(Long idReservation) throws NoReservationFoundException {
        if (reservationRepository.existsById(idReservation)) {
            reservationRepository.deleteById(idReservation);
            return;
        }
        throw new NoReservationFoundException("No reservation found with given ID");
    }

    public Reservation extendReservation(Long idReservation) throws NoReservationFoundException {
        Optional<Reservation> reservation = reservationRepository.findById(idReservation);
        if (reservation.isPresent()) {
            reservation.get().setDueDate(reservation.get().getDueDate().plusDays(1));
            reservationRepository.save(reservation.get());
            return reservation.get();
        }
        throw new NoReservationFoundException("No reservation found");
    }

    private boolean canUserReserveBook(User user) {
        Long reservationCount = reservationRepository.countByUser_UserId(user.getUserId());
        return reservationCount < MAX_RESERVATIONS;
    }

    public int getMAX_RESERVATIONS() {
        return MAX_RESERVATIONS;
    }
}

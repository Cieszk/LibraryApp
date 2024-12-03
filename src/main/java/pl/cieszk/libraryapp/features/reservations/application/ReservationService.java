package pl.cieszk.libraryapp.features.reservations.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.core.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.core.exceptions.custom.NoReservationFoundException;
import pl.cieszk.libraryapp.features.auth.application.dto.UserRequestDto;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.books.application.BookInstanceService;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceResponseDto;
import pl.cieszk.libraryapp.features.books.application.mapper.BookInstanceMapper;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.reservations.application.dto.ReservationRequestDto;
import pl.cieszk.libraryapp.features.reservations.application.dto.ReservationResponseDto;
import pl.cieszk.libraryapp.features.reservations.application.mapper.ReservationMapper;
import pl.cieszk.libraryapp.features.reservations.domain.Reservation;
import pl.cieszk.libraryapp.features.reservations.repository.ReservationRepository;
import pl.cieszk.libraryapp.shared.dto.BookUserRequest;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ReservationService {

    private ReservationRepository reservationRepository;
    private BookInstanceService bookInstanceService;
    private ReservationMapper reservationMapper;
    private BookInstanceMapper bookInstanceMapper;

    private final int MAX_RESERVATIONS = 3;

    public ReservationResponseDto createReservation(BookUserRequest bookUserRequest) throws BookNotAvailableException {
        Book book = bookUserRequest.toBook();
        User user = bookUserRequest.toUser();
        if (!canUserReserveBook(user)) {
            throw new BookNotAvailableException("User has reached maximum number of reservations");
        }
        BookInstanceResponseDto availableBookInstance = bookInstanceService.getAnyAvailable(book);
        Reservation reservation = Reservation.builder()
                .bookInstance(bookInstanceMapper.toEntity(availableBookInstance))
                .user(user)
                .build();
        reservationRepository.save(reservation);
        return reservationMapper.toResponseDto(reservation);
    }

    public ReservationResponseDto findReservationByUserAndBook(BookUserRequest bookUserRequest) throws NoReservationFoundException {
        Optional<Reservation> reservation = reservationRepository.findByUserAndBookInstance_Book(bookUserRequest.toUser(), bookUserRequest.toBook());
        if (reservation.isPresent()){
            return reservationMapper.toResponseDto(reservation.get());
        }
        throw new NoReservationFoundException("No reservation found for user and book");
    }

    public void deleteReservation(BookUserRequest bookUserRequest) throws NoReservationFoundException {
        User user = bookUserRequest.toUser();
        Book book = bookUserRequest.toBook();
        if (reservationRepository.existsByUserAndBookInstance_Book(user, book)) {
            reservationRepository.deleteByUserAAndAndBookInstance_Book(user, book);
            return;
        }
        throw new NoReservationFoundException("No reservation found with given ID");
    }

    public ReservationResponseDto extendReservation(BookUserRequest bookUserRequest) throws NoReservationFoundException {
        Optional<Reservation> reservation = reservationRepository.findByUserAndBookInstance_Book(bookUserRequest.toUser(), bookUserRequest.toBook());
        if (reservation.isPresent()) {
            reservation.get().setDueDate(reservation.get().getDueDate().plusDays(1));
            reservationRepository.save(reservation.get());
            return reservationMapper.toResponseDto(reservation.get());
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

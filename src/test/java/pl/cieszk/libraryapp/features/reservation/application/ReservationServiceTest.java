package pl.cieszk.libraryapp.features.reservation.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cieszk.libraryapp.core.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.core.exceptions.custom.NoReservationFoundException;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.books.application.BookInstanceService;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.books.repository.BookInstanceRepository;
import pl.cieszk.libraryapp.features.reservations.application.ReservationService;
import pl.cieszk.libraryapp.features.reservations.domain.Reservation;
import pl.cieszk.libraryapp.features.reservations.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookInstanceRepository bookInstanceRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private BookInstanceService bookInstanceService;

    private User user;

    private Book book;

    private BookInstance bookInstance;


    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId(1L)
                .build();
        book = Book.builder()
                .bookId(1L)
                .build();
        bookInstance = BookInstance.builder()
                .bookInstanceId(1L)
                .book(book)
                .build();
    }

    @Test
    void createReservation_ShouldReturnReservation() throws BookNotAvailableException {
        // Given
        Reservation reservation = Reservation.builder()
                .bookInstance(bookInstance)
                .user(user)
                .build();
        when(bookInstanceService.getAnyAvailable(book)).thenReturn(bookInstance);
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        // When
        Reservation result = reservationService.createReservation(book, user);

        // Then
        assertEquals(reservation, result);
        verify(reservationRepository, times(1)).save(any());
        verify(bookInstanceService, times(1)).getAnyAvailable(book);
    }

    @Test
    void createReservation_ShouldThrowBookNotAvailableExceptionWhenUserExceededBookReservationLimit() throws BookNotAvailableException {
        // Given
        when(reservationRepository.countByUser_UserId(user.getUserId())).thenReturn(3L);
        // When
        BookNotAvailableException exception = assertThrows(BookNotAvailableException.class, () -> reservationService.createReservation(book, user));

        // Then
        assertEquals("User has reached maximum number of reservations", exception.getMessage());
        verify(reservationRepository, times(0)).save(any());
        verify(bookInstanceService, never()).getAnyAvailable(book);
    }

    @Test
    void findReservationByUserAndBook_ShouldReturnReservation() throws NoReservationFoundException {
        // Given
        Reservation reservation = Reservation.builder()
                .bookInstance(bookInstance)
                .user(user)
                .build();
        when(reservationRepository.findByUserAndBookInstance_Book(user, book)).thenReturn(Optional.of(reservation));

        // When
        Reservation result = reservationService.findReservationByUserAndBook(user, book);

        // Then
        assertEquals(reservation, result);
        verify(reservationRepository, times(1)).findByUserAndBookInstance_Book(user, book);
    }

    @Test
    void findReservationByUserAndBook_ShouldThrowNoReservationFoundException() {
        // Given
        when(reservationRepository.findByUserAndBookInstance_Book(user, book)).thenReturn(empty());

        // When
        NoReservationFoundException exception = assertThrows(NoReservationFoundException.class, () -> reservationService.findReservationByUserAndBook(user, book));

        // Then
        assertEquals("No reservation found for user and book", exception.getMessage());
        verify(reservationRepository, times(1)).findByUserAndBookInstance_Book(user, book);
    }

    @Test
    void deleteReservation_ShouldDeleteReservation() throws NoReservationFoundException {
        // Given
        Long idReservation = 1L;

        when(reservationRepository.existsById(idReservation)).thenReturn(true);

        // When
        reservationService.deleteReservation(idReservation);

        // Then
        verify(reservationRepository, times(1)).deleteById(idReservation);
    }

    @Test
    void deleteReservation_ShouldThrowNoReservationFoundException() {
        // Given
        Long idReservation = 1L;
        when(reservationRepository.existsById(idReservation)).thenReturn(false);

        // When
        NoReservationFoundException exception = assertThrows(NoReservationFoundException.class, () -> reservationService.deleteReservation(idReservation));

        // Then
        assertEquals("No reservation found with given ID", exception.getMessage());
        verify(reservationRepository, times(0)).deleteById(idReservation);
    }

    @Test
    void extendReservation_ShouldReturnExtendedReservation() throws NoReservationFoundException {
        // Given
        Reservation reservation = Reservation.builder()
                .bookInstance(bookInstance)
                .user(user)
                .dueDate(LocalDateTime.now())
                .build();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        // When
        Reservation result = reservationService.extendReservation(1L);

        // Then
        assertNotNull(result.getDueDate());
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void extendReservation_ShouldThrowNoReservationFoundException() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(empty());

        // When
        NoReservationFoundException exception = assertThrows(NoReservationFoundException.class, () -> reservationService.extendReservation(1L));

        // Then
        assertEquals("No reservation found", exception.getMessage());
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(0)).save(any());
    }
}

package pl.cieszk.libraryapp.features.reservation.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cieszk.libraryapp.core.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.core.exceptions.custom.NoReservationFoundException;
import pl.cieszk.libraryapp.features.auth.application.mapper.UserMapper;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.books.application.BookInstanceService;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceResponseDto;
import pl.cieszk.libraryapp.features.books.application.mapper.BookMapper;
import pl.cieszk.libraryapp.features.books.application.mapper.BookInstanceMapper;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.books.repository.BookInstanceRepository;
import pl.cieszk.libraryapp.features.reservations.application.ReservationService;
import pl.cieszk.libraryapp.features.reservations.application.dto.ReservationRequestDto;
import pl.cieszk.libraryapp.features.reservations.application.dto.ReservationResponseDto;
import pl.cieszk.libraryapp.features.reservations.application.mapper.ReservationMapper;
import pl.cieszk.libraryapp.features.reservations.domain.Reservation;
import pl.cieszk.libraryapp.features.reservations.repository.ReservationRepository;
import pl.cieszk.libraryapp.shared.dto.BookUserRequest;

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

    @Mock
    private UserMapper userMapper;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookInstanceMapper bookInstanceMapper;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private BookInstanceService bookInstanceService;

    private User user;

    private Book book;
    private Reservation reservation;

    private BookInstance bookInstance;
    private BookInstanceResponseDto bookInstanceResponseDto;

    private BookUserRequest bookUserRequest;

    private ReservationResponseDto reservationResponseDto;

    private ReservationRequestDto reservationRequestDto;


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
        reservation = Reservation.builder()
                .reservationId(1L)
                .bookInstance(bookInstance)
                .user(user)
                .dueDate(LocalDateTime.now())
                .build();

        bookInstanceResponseDto = BookInstanceResponseDto.builder()
                .build();

        bookUserRequest = BookUserRequest.builder()
                .userMapper(userMapper)
                .bookMapper(bookMapper)
                .build();

        reservationResponseDto = ReservationResponseDto.builder()
                .dueDate(LocalDateTime.now())
                .build();

        reservationRequestDto = ReservationRequestDto.builder()
                .build();
    }

    @Test
    void createReservation_ShouldReturnReservation() throws BookNotAvailableException {
        when(bookUserRequest.toBook()).thenReturn(book);
        when(bookUserRequest.toUser()).thenReturn(user);
        when(bookInstanceService.getAnyAvailable(book)).thenReturn(bookInstanceResponseDto);
        when(bookInstanceMapper.toEntity(bookInstanceResponseDto)).thenReturn(bookInstance);
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationMapper.toResponseDto(any(Reservation.class)))
                .thenReturn(reservationResponseDto);

        // When
        ReservationResponseDto result = reservationService.createReservation(bookUserRequest);

        // Then
        assertEquals(reservationResponseDto, result);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(bookInstanceService, times(1)).getAnyAvailable(book);
    }

    @Test
    void createReservation_ShouldThrowBookNotAvailableExceptionWhenUserExceededBookReservationLimit() throws BookNotAvailableException {
        // Given
        when(reservationRepository.countByUser_UserId(user.getUserId())).thenReturn(3L);
        when(bookUserRequest.toBook()).thenReturn(book);
        when(bookUserRequest.toUser()).thenReturn(user);
        // When
        BookNotAvailableException exception = assertThrows(BookNotAvailableException.class, () -> reservationService.createReservation(bookUserRequest));

        // Then
        assertEquals("User has reached maximum number of reservations", exception.getMessage());
        verify(reservationRepository, times(0)).save(any());
        verify(bookInstanceService, never()).getAnyAvailable(book);
    }

    @Test
    void findReservationByUserAndBook_ShouldReturnReservation() throws NoReservationFoundException {
        // Given
        when(reservationMapper.toResponseDto(reservation)).thenReturn(reservationResponseDto);
        when(bookUserRequest.toBook()).thenReturn(book);
        when(bookUserRequest.toUser()).thenReturn(user);
        when(reservationRepository.findByUserAndBookInstance_Book(user, book)).thenReturn(Optional.of(reservation));

        // When
        ReservationResponseDto result = reservationService.findReservationByUserAndBook(bookUserRequest);

        // Then
        assertEquals(reservationResponseDto, result);
        verify(reservationRepository, times(1)).findByUserAndBookInstance_Book(user, book);
    }

    @Test
    void findReservationByUserAndBook_ShouldThrowNoReservationFoundException() {
        // Given
        when(bookUserRequest.toBook()).thenReturn(book);
        when(bookUserRequest.toUser()).thenReturn(user);
        when(reservationRepository.findByUserAndBookInstance_Book(user, book)).thenReturn(empty());

        // When
        NoReservationFoundException exception = assertThrows(NoReservationFoundException.class, () -> reservationService.findReservationByUserAndBook(bookUserRequest));

        // Then
        assertEquals("No reservation found for user and book", exception.getMessage());
        verify(reservationRepository, times(1)).findByUserAndBookInstance_Book(user, book);
    }

    @Test
    void deleteReservation_ShouldDeleteReservation() throws NoReservationFoundException {
        // Given
        when(bookUserRequest.toBook()).thenReturn(book);
        when(bookUserRequest.toUser()).thenReturn(user);
        when(reservationRepository.existsByUserAndBookInstance_Book(user, book)).thenReturn(true);

        // When
        reservationService.deleteReservation(bookUserRequest);

        // Then
        verify(reservationRepository, times(1)).deleteByUserAAndAndBookInstance_Book(user, book);
    }

    @Test
    void deleteReservation_ShouldThrowNoReservationFoundException() {
        // Given
        Long idReservation = 1L;
        when(bookUserRequest.toBook()).thenReturn(book);
        when(bookUserRequest.toUser()).thenReturn(user);
        when(reservationRepository.existsByUserAndBookInstance_Book(user, book)).thenReturn(false);

        // When
        NoReservationFoundException exception = assertThrows(NoReservationFoundException.class, () -> reservationService.deleteReservation(bookUserRequest));

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
        when(bookUserRequest.toBook()).thenReturn(book);
        when(bookUserRequest.toUser()).thenReturn(user);
        when(reservationRepository.findByUserAndBookInstance_Book(user, book)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        when(reservationMapper.toResponseDto(reservation)).thenReturn(reservationResponseDto);

        // When
        ReservationResponseDto result = reservationService.extendReservation(bookUserRequest);

        // Then
        assertNotNull(result.getDueDate());
        verify(reservationRepository, times(1)).findByUserAndBookInstance_Book(user, book);
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void extendReservation_ShouldThrowNoReservationFoundException() {
        // Given
        when(bookUserRequest.toBook()).thenReturn(book);
        when(bookUserRequest.toUser()).thenReturn(user);
        when(reservationRepository.findByUserAndBookInstance_Book(user, book)).thenReturn(empty());

        // When
        NoReservationFoundException exception = assertThrows(NoReservationFoundException.class, () -> reservationService.extendReservation(bookUserRequest));

        // Then
        assertEquals("No reservation found", exception.getMessage());
        verify(reservationRepository, times(1)).findByUserAndBookInstance_Book(user, book);
        verify(reservationRepository, times(0)).save(any());
    }
}

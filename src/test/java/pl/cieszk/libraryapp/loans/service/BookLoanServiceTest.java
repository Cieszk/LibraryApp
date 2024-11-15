package pl.cieszk.libraryapp.loans.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cieszk.libraryapp.auth.model.User;
import pl.cieszk.libraryapp.books.model.Book;
import pl.cieszk.libraryapp.books.model.BookInstance;
import pl.cieszk.libraryapp.books.repository.BookInstanceRepository;
import pl.cieszk.libraryapp.books.service.BookInstanceService;
import pl.cieszk.libraryapp.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.loans.model.BookLoan;
import pl.cieszk.libraryapp.loans.repository.BookLoanRepository;
import pl.cieszk.libraryapp.reservations.model.Reservation;
import pl.cieszk.libraryapp.reservations.repository.ReservationRepository;
import pl.cieszk.libraryapp.reservations.service.ReservationService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookLoanServiceTest {

    @Mock
    private BookLoanRepository bookLoanRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookInstanceRepository bookInstanceRepository;

    @Mock
    private BookInstanceService bookInstanceService;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private BookLoanService bookLoanService;


    @Test
    void createLoan_ShouldReturnCreatedBookLoanWhenBookIsNotReserved() throws BookNotAvailableException {
        // Given
        Book book = Book.builder()
                .bookId(1L)
                .build();

        User user = User.builder()
                .userId(1L)
                .build();

        BookInstance bookInstance = BookInstance.builder()
                .bookInstanceId(1L)
                .book(book)
                .build();

        BookLoan savedBookLoan = BookLoan.builder()
                .bookInstance(bookInstance)
                .user(user)
                .build();

        when(bookLoanRepository.save(any(BookLoan.class))).thenReturn(savedBookLoan);
        when(reservationService.findReservationByUserAndBook(user, book)).thenReturn(Optional.empty());
        when(bookInstanceService.getAnyAvailable(book)).thenReturn(Optional.of(bookInstance));

        // When
        bookLoanService.createLoan(book, user);

        // Then
        verify(bookLoanRepository, times(1)).save(any(BookLoan.class));
        verify(reservationService, never()).deleteReservation(any(Reservation.class));
        assertEquals(savedBookLoan.getBookLoanId(), bookLoanService.createLoan(book, user).getBookLoanId());
    }

    @Test
    void createLoan_ShouldReturnCreatedBookLoanWhenBookIsReserved() throws BookNotAvailableException {
        // Given
        Book book = Book.builder()
                .bookId(1L)
                .build();

        User user = User.builder()
                .userId(1L)
                .build();

        BookInstance bookInstance = BookInstance.builder()
                .bookInstanceId(1L)
                .book(book)
                .build();

        Reservation reservation = Reservation.builder()
                .bookInstance(bookInstance)
                .user(user)
                .build();

        BookLoan savedBookLoan = BookLoan.builder()
                .bookInstance(bookInstance)
                .user(user)
                .loanDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusWeeks(2))
                .build();

        when(bookLoanRepository.save(any(BookLoan.class))).thenReturn(savedBookLoan);
        when(reservationService.findReservationByUserAndBook(user, book)).thenReturn(Optional.of(reservation));

        // When
        bookLoanService.createLoan(book, user);

        // Then
        verify(bookLoanRepository, times(1)).save(any(BookLoan.class));
        verify(reservationService, times(1)).deleteReservation(any(Reservation.class));
        assertEquals(savedBookLoan.getBookLoanId(), bookLoanService.createLoan(book, user).getBookLoanId());
    }

    @Test
    void createLoan_ShouldThrowBookNotAvailableExceptionWhenBookIsNotAvailable() {
        // Given
        Book book = Book.builder()
                .bookId(1L)
                .build();

        User user = User.builder()
                .userId(1L)
                .build();

        when(reservationService.findReservationByUserAndBook(user, book)).thenReturn(Optional.empty());
        when(bookInstanceService.getAnyAvailable(book)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotAvailableException.class, () -> bookLoanService.createLoan(book, user));

        verify(bookInstanceService, times(1)).getAnyAvailable(book);
        verify(bookLoanRepository, never()).save(any(BookLoan.class));
        verify(reservationService, never()).deleteReservation(any(Reservation.class));

    }

    @Test
    void returnBook_ShouldReturnBookLoanWithReturnDate() {
        // Given
        Book book = Book.builder()
                .bookId(1L)
                .build();

        User user = User.builder()
                .userId(1L)
                .build();

        BookInstance bookInstance = BookInstance.builder()
                .bookInstanceId(1L)
                .book(book)
                .build();

        BookLoan bookLoan = BookLoan.builder()
                .bookInstance(bookInstance)
                .user(user)
                .loanDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusWeeks(2))
                .build();

        when(bookLoanRepository.findByUserAndBookInstance_Book(user, book)).thenReturn(Optional.of(bookLoan));

        // When
        bookLoanService.returnBook(book, user);

        // Then
        verify(bookLoanRepository, times(1)).save(any(BookLoan.class));
        assertEquals(LocalDateTime.now().toLocalDate(), bookLoan.getReturnDate().toLocalDate());
    }

    @Test
    void returnBook_ShouldThrowIllegalArgumentExceptionWhenBookIsNotLoanedByUser() {
        // Given
        Book book = Book.builder()
                .bookId(1L)
                .build();

        User user = User.builder()
                .userId(1L)
                .build();

        when(bookLoanRepository.findByUserAndBookInstance_Book(user, book)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> bookLoanService.returnBook(book, user));

        verify(bookLoanRepository, never()).save(any(BookLoan.class));
    }

    @Test
    void canUserLoanBook_ShouldReturnTrueWhenUserHasLessThanMaxLoans() {
        // Given
        User user = User.builder()
                .userId(1L)
                .build();

        when(bookLoanRepository.findByUser_UserId(user.getUserId())).thenReturn(Collections.nCopies(3, new BookLoan()));

        // When
        boolean result = bookLoanService.canUserLoanBook(user);

        // Then
        assertTrue(result);
    }

    @Test
    void canUserLoanBook_ShouldReturnFalseWhenUserHasMaxLoans() {
        // Given
        User user = User.builder()
                .userId(1L)
                .build();

        when(bookLoanRepository.findByUser_UserId(user.getUserId())).thenReturn(Collections.nCopies(5, new BookLoan()));

        // When
        boolean result = bookLoanService.canUserLoanBook(user);

        // Then
        assertFalse(result);
    }

    @Test
    void hasActiveLoan_ShouldReturnTrueWhenUserHasActiveLoan() {
        // Given
        Book book = Book.builder()
                .bookId(1L)
                .build();

        User user = User.builder()
                .userId(1L)
                .build();

        BookInstance bookInstance = BookInstance.builder()
                .bookInstanceId(1L)
                .book(book)
                .build();

        BookLoan bookLoan = BookLoan.builder()
                .bookInstance(bookInstance)
                .user(user)
                .loanDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusWeeks(2))
                .build();

        when(bookLoanRepository.findByUserAndBookInstance_Book(user, book)).thenReturn(Optional.of(bookLoan));

        // When
        boolean result = bookLoanService.hasActiveLoan(book, user);

        // Then
        assertTrue(result);
    }

    @Test
    void hasActiveLoan_ShouldReturnFalseWhenUserHasNoActiveLoan() {
        // Given
        Book book = Book.builder()
                .bookId(1L)
                .build();

        User user = User.builder()
                .userId(1L)
                .build();

        when(bookLoanRepository.findByUserAndBookInstance_Book(user, book)).thenReturn(Optional.empty());

        // When
        boolean result = bookLoanService.hasActiveLoan(book, user);

        // Then
        assertFalse(result);
    }
}

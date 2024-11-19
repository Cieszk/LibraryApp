package pl.cieszk.libraryapp.features.loans.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.books.repository.BookInstanceRepository;
import pl.cieszk.libraryapp.features.books.application.BookInstanceService;
import pl.cieszk.libraryapp.core.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.features.loans.application.BookLoanService;
import pl.cieszk.libraryapp.features.loans.domain.BookLoan;
import pl.cieszk.libraryapp.features.loans.repository.BookLoanRepository;
import pl.cieszk.libraryapp.features.reservations.domain.Reservation;
import pl.cieszk.libraryapp.features.reservations.repository.ReservationRepository;
import pl.cieszk.libraryapp.features.reservations.application.ReservationService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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

    @Test
    void renewLoan_ShouldReturnBookLoanWithIncreasedRenewCountAndDueDate() {
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
                .renewCount(1)
                .build();

        when(bookLoanRepository.findByUserAndBookInstance_Book(user, book)).thenReturn(Optional.of(bookLoan));

        // When
        bookLoanService.renewLoan(book, user);

        // Then
        verify(bookLoanRepository, times(1)).save(any(BookLoan.class));
        assertEquals(2, bookLoan.getRenewCount());
        assertEquals(LocalDateTime.now().plusWeeks(4).toLocalDate(), bookLoan.getDueDate().toLocalDate());
    }

    @Test
    void renewLoan_ShouldThrowIllegalArgumentExceptionWhenBookIsNotLoanedByUser() {
        // Given
        Book book = Book.builder()
                .bookId(1L)
                .build();

        User user = User.builder()
                .userId(1L)
                .build();

        when(bookLoanRepository.findByUserAndBookInstance_Book(user, book)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> bookLoanService.renewLoan(book, user));

        verify(bookLoanRepository, never()).save(any(BookLoan.class));
    }

    @Test
    void renewLoan_ShouldThrowIllegalArgumentExceptionWhenBookCannotBeRenewedMoreThanTwice() {
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
                .renewCount(2)
                .build();

        when(bookLoanRepository.findByUserAndBookInstance_Book(user, book)).thenReturn(Optional.of(bookLoan));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> bookLoanService.renewLoan(book, user));

        verify(bookLoanRepository, never()).save(any(BookLoan.class));
    }

    @Test
    void getCurrentUserLoans_ShouldReturnBookLoansForCurrentUser() {
        // Given
        User user = User.builder()
                .userId(1L)
                .build();

        Book book = Book.builder()
                .bookId(1L)
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

        when(bookLoanRepository.findByUser_UserId(user.getUserId())).thenReturn(List.of(bookLoan));

        // When & Then
        assertEquals(bookLoanService.getCurrentUserLoans(user.getUserId()), List.of(bookLoan));
        verify(bookLoanRepository, times(1)).findByUser_UserId(user.getUserId());
    }

    @Test
    void getCurrentUserLoans_ShouldReturnEmptyListWhenThereIsNoLoans() {
        // Given
        User user = User.builder()
                .userId(1L)
                .build();

        when(bookLoanRepository.findByUser_UserId(user.getUserId())).thenReturn(Collections.emptyList());

        // When & Then
        assertEquals(bookLoanService.getCurrentUserLoans(user.getUserId()), Collections.emptyList());
        verify(bookLoanRepository, times(1)).findByUser_UserId(user.getUserId());
    }

    @Test
    void getLoanHistory_ShouldReturnListOfLoanBook() {
        // Given
        User user = User.builder()
                .userId(1L)
                .build();

        Book book = Book.builder()
                .bookId(1L)
                .build();

        BookInstance bookInstance = BookInstance.builder()
                .bookInstanceId(1L)
                .book(book)
                .build();

        BookLoan bookLoanFirst = BookLoan.builder()
                .bookLoanId(1L)
                .loanDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusWeeks(2))
                .returnDate(LocalDateTime.now().plusWeeks(1))
                .bookInstance(bookInstance)
                .user(user)
                .build();

        BookLoan bookLoanSecond = BookLoan.builder()
                .bookLoanId(2L)
                .loanDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusWeeks(2))
                .returnDate(LocalDateTime.now().plusWeeks(1))
                .bookInstance(bookInstance)
                .user(user)
                .build();

        when(bookLoanRepository.findByUser_UserIdAndReturnDateIsNotNull(user.getUserId())).thenReturn(List.of(bookLoanFirst, bookLoanSecond));

        // When
        List<BookLoan> result = bookLoanService.getLoanHistory(user.getUserId());

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        BookLoan firstLoan = result.get(0);
        assertEquals(bookLoanFirst.getBookLoanId(), firstLoan.getBookLoanId());
        assertEquals(bookLoanFirst.getUser(), firstLoan.getUser());
        assertNotNull(firstLoan.getReturnDate());

        BookLoan secondLoan = result.get(1);
        assertEquals(bookLoanSecond.getBookLoanId(), secondLoan.getBookLoanId());
        assertEquals(bookLoanSecond.getUser(), secondLoan.getUser());
        assertNotNull(secondLoan.getReturnDate());
    }

    @Test
    void getLoanHistory_ShouldReturnEmptyListWhenThereIsNoLoanHistory() {
        // Given
        User user = User.builder()
                .userId(1L)
                .build();

        when(bookLoanRepository.findByUser_UserIdAndReturnDateIsNotNull(user.getUserId())).thenReturn(Collections.emptyList());

        // When & Then
        assertEquals(bookLoanService.getLoanHistory(user.getUserId()), Collections.emptyList());
    }

    @Test
    void getUserFines_ShouldReturnMapOfBookAndFineAmount() {
        // Given
        User user = User.builder()
                .userId(1L)
                .build();

        Book book = Book.builder()
                .bookId(1L)
                .build();

        BookInstance bookInstance = BookInstance.builder()
                .bookInstanceId(1L)
                .book(book)
                .build();

        BookLoan bookLoan = BookLoan.builder()
                .bookLoanId(1L)
                .loanDate(LocalDateTime.now().minusWeeks(1))
                .dueDate(LocalDateTime.now().minusDays(1))
                .bookInstance(bookInstance)
                .fineAmount(10.0)
                .user(user)
                .build();

        when(bookLoanRepository.findByUser_UserId(user.getUserId())).thenReturn(List.of(bookLoan));

        // When
        var result = bookLoanService.getUserFines(user.getUserId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(bookInstance));
        assertEquals(10.0, result.get(bookInstance));
    }
}

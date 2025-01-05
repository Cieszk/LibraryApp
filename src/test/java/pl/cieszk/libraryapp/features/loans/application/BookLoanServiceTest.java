package pl.cieszk.libraryapp.features.loans.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cieszk.libraryapp.core.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.core.exceptions.custom.NoReservationFoundException;
import pl.cieszk.libraryapp.features.auth.application.dto.UserRequestDto;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.books.application.BookInstanceService;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceFineDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceResponseDto;
import pl.cieszk.libraryapp.features.books.application.mapper.BookInstanceFineMapper;
import pl.cieszk.libraryapp.features.books.application.mapper.BookInstanceMapper;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.loans.application.dto.BookLoanResponseDto;
import pl.cieszk.libraryapp.features.loans.application.mapper.BookLoanMapper;
import pl.cieszk.libraryapp.features.loans.domain.BookLoan;
import pl.cieszk.libraryapp.features.loans.repository.BookLoanRepository;
import pl.cieszk.libraryapp.features.reservations.application.ReservationService;
import pl.cieszk.libraryapp.features.reservations.application.dto.ReservationResponseDto;
import pl.cieszk.libraryapp.shared.dto.BookUserRequest;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookLoanServiceTest {

    @Mock
    private BookLoanRepository bookLoanRepository;

    @Mock
    private BookInstanceService bookInstanceService;

    @Mock
    private ReservationService reservationService;

    @Mock
    private BookLoanMapper bookLoanMapper;

    @Mock
    private BookInstanceMapper bookInstanceMapper;

    @Mock
    private BookInstanceFineMapper bookInstanceFineMapper;

    @InjectMocks
    private BookLoanService bookLoanService;

    private Book book;
    private User user;
    private BookInstance bookInstance;
    private BookLoan bookLoan;
    private BookUserRequest bookUserRequest;
    private BookInstanceResponseDto bookInstanceResponseDto;
    private ReservationResponseDto reservationResponseDto;
    private BookLoanResponseDto bookLoanResponseDto;
    private UserRequestDto userRequestDto;
    private BookInstanceFineDto bookInstanceFineDto;

    @BeforeEach
    void setUp() {
        user = User.builder().userId(1L).email("email").build();
        book = Book.builder().bookId(1L).build();
        bookInstance = mock(BookInstance.class);
        bookLoan = BookLoan.builder()
            .bookLoanId(1L)
            .user(user)
            .bookInstance(bookInstance)
            .loanDate(LocalDateTime.now())
            .dueDate(LocalDateTime.now().plusWeeks(2))
            .renewCount(0)
            .build();
        bookUserRequest = mock(BookUserRequest.class);
        bookInstanceResponseDto = mock(BookInstanceResponseDto.class);
        reservationResponseDto = mock(ReservationResponseDto.class);
        bookLoanResponseDto = mock(BookLoanResponseDto.class);
        userRequestDto = mock(UserRequestDto.class);
        bookInstanceFineDto = mock(BookInstanceFineDto.class);
    }

    @Test
    void createLoan_ShouldThrowExceptionWhenBookIsNotAvailableAndNoReservationExists() throws BookNotAvailableException, NoReservationFoundException {
        when(bookUserRequest.toBook()).thenReturn(book);
        when(reservationService.findReservationByUserAndBook(bookUserRequest))
                .thenThrow(NoReservationFoundException.class);
        when(bookInstanceService.getAnyAvailable(book))
                .thenThrow(new BookNotAvailableException("Book is not available"));

        assertThrows(BookNotAvailableException.class, () -> bookLoanService.createLoan(bookUserRequest));

        verify(bookInstanceService).getAnyAvailable(book);
        verify(bookLoanRepository, never()).save(any(BookLoan.class));
    }

    @Test
    void returnBook_ShouldReturnBookLoanWithReturnDate() {
        when(bookUserRequest.toUser()).thenReturn(user);
        when(bookUserRequest.toBook()).thenReturn(book);
        when(bookLoanRepository.save(bookLoan)).thenReturn(bookLoan);
        when(bookLoanRepository.findByUserAndBookInstance_Book(user, book))
                .thenReturn(Optional.of(bookLoan));
        when(bookLoanMapper.toResponseDto(bookLoan)).thenReturn(bookLoanResponseDto);


        BookLoanResponseDto result = bookLoanService.returnBook(bookUserRequest);

        assertEquals(bookLoanResponseDto, result);
        verify(bookLoanRepository).save(bookLoan);
        assertNotNull(bookLoan.getReturnDate());
    }

    @Test
    void returnBook_ShouldThrowIllegalArgumentExceptionWhenBookIsNotLoanedByUser() {
        when(bookUserRequest.toUser()).thenReturn(user);
        when(bookUserRequest.toBook()).thenReturn(book);
        when(bookLoanRepository.findByUserAndBookInstance_Book(user, book))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bookLoanService.returnBook(bookUserRequest));

        verify(bookLoanRepository, never()).save(any(BookLoan.class));
    }

    @Test
    void canUserLoanBook_ShouldReturnTrueWhenUserHasLessThanMaxLoans() {
        BookLoan bookLoan2 = BookLoan.builder().bookLoanId(2L).build();
        BookLoan bookLoan3 = BookLoan.builder().bookLoanId(3L).build();
        when(userRequestDto.getEmail()).thenReturn("email");
        when(bookLoanRepository.findByUser_Email(user.getEmail()))
                .thenReturn(Set.of(bookLoan, bookLoan2, bookLoan3));

        boolean result = bookLoanService.canUserLoanBook(userRequestDto);

        assertTrue(result);
    }

    @Test
    void canUserLoanBook_ShouldReturnFalseWhenUserHasMaxLoans() {
        BookLoan bookloan2 = BookLoan.builder().bookLoanId(2L).build();
        BookLoan bookloan3 = BookLoan.builder().bookLoanId(3L).build();
        BookLoan bookloan4 = BookLoan.builder().bookLoanId(4L).build();
        BookLoan bookloan5 = BookLoan.builder().bookLoanId(5L).build();
        when(userRequestDto.getEmail()).thenReturn(user.getEmail());
        when(bookLoanRepository.findByUser_Email(user.getEmail()))
                .thenReturn(Set.of(bookLoan, bookloan2, bookloan3, bookloan4, bookloan5));

        boolean result = bookLoanService.canUserLoanBook(userRequestDto);

        assertFalse(result);
    }

    @Test
    void hasActiveLoan_ShouldReturnTrueWhenUserHasActiveLoan() {
        when(bookUserRequest.toUser()).thenReturn(user);
        when(bookUserRequest.toBook()).thenReturn(book);
        when(bookLoanRepository.findByUserAndBookInstance_Book(user, book))
                .thenReturn(Optional.of(bookLoan));

        boolean result = bookLoanService.hasActiveLoan(bookUserRequest);

        assertTrue(result);
    }

    @Test
    void hasActiveLoan_ShouldReturnFalseWhenUserHasNoActiveLoan() {
        when(bookLoanRepository.findByUserAndBookInstance_Book(user, book))
                .thenReturn(Optional.empty());
        when(bookUserRequest.toUser()).thenReturn(user);
        when(bookUserRequest.toBook()).thenReturn(book);

        boolean result = bookLoanService.hasActiveLoan(bookUserRequest);

        assertFalse(result);
    }

    @Test
    void renewLoan_ShouldIncreaseRenewCountAndUpdateDueDate() {
        when(bookLoanRepository.findByUserAndBookInstance_Book(user, book))
                .thenReturn(Optional.of(bookLoan));
        when(bookLoanMapper.toResponseDto(any(BookLoan.class))).thenReturn(bookLoanResponseDto);
        when(bookUserRequest.toUser()).thenReturn(user);
        when(bookUserRequest.toBook()).thenReturn(book);

        BookLoanResponseDto result = bookLoanService.renewLoan(bookUserRequest);

        assertEquals(bookLoanResponseDto, result);
        assertEquals(1, bookLoan.getRenewCount());
        assertEquals(LocalDateTime.now().plusWeeks(4).toLocalDate(), bookLoan.getDueDate().toLocalDate());
        verify(bookLoanRepository).save(any(BookLoan.class));
    }

    @Test
    void renewLoan_ShouldThrowExceptionWhenBookCannotBeRenewedMoreThanTwice() {
        bookLoan.setRenewCount(2);
        when(bookLoanRepository.findByUserAndBookInstance_Book(user, book))
                .thenReturn(Optional.of(bookLoan));
        when(bookUserRequest.toUser()).thenReturn(user);
        when(bookUserRequest.toBook()).thenReturn(book);

        assertThrows(IllegalArgumentException.class, () -> bookLoanService.renewLoan(bookUserRequest));

        verify(bookLoanRepository, never()).save(any(BookLoan.class));
    }

    @Test
    void getCurrentUserLoans_ShouldReturnBookLoansForUser() {
        when(userRequestDto.getEmail()).thenReturn(user.getEmail());
        when(bookLoanRepository.findByUser_Email(user.getEmail())).thenReturn(Set.of(bookLoan));
        when(bookLoanMapper.toResponseDtos(anySet())).thenReturn(Set.of(bookLoanResponseDto));

        Set<BookLoanResponseDto> result = bookLoanService.getCurrentUserLoans(userRequestDto);

        assertEquals(Set.of(bookLoanResponseDto), result);
        verify(bookLoanRepository).findByUser_Email(anyString());
    }

    @Test
    void getLoanHistory_ShouldReturnUserLoanHistory() {
        BookLoan bookLoan2 = BookLoan.builder().bookLoanId(2L).build();
        when(userRequestDto.getEmail()).thenReturn(user.getEmail());
        when(bookLoanRepository.findByUser_EmailAndReturnDateIsNotNull(user.getEmail()))
                .thenReturn(Set.of(bookLoan, bookLoan2));
        when(bookLoanMapper.toResponseDtos(anySet())).thenReturn(Set.of(bookLoanResponseDto));

        Set<BookLoanResponseDto> result = bookLoanService.getLoanHistory(userRequestDto);

        assertEquals(Set.of(bookLoanResponseDto), result);
        verify(bookLoanRepository).findByUser_EmailAndReturnDateIsNotNull(anyString());
    }

    @Test
    void getUserFines_ShouldReturnMapOfBooksAndFineAmounts() {
        bookLoan.setFineAmount(10.0);
        when(userRequestDto.getEmail()).thenReturn(user.getEmail());
        when(bookLoanRepository.findByUser_Email(user.getEmail())).thenReturn(Set.of(bookLoan));
        when(bookInstanceFineMapper.toDto(bookInstance, 10.0)).thenReturn(bookInstanceFineDto);

        List<BookInstanceFineDto> result = bookLoanService.getUserFines(userRequestDto);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}

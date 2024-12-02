package pl.cieszk.libraryapp.features.loans.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.core.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.core.exceptions.custom.NoReservationFoundException;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.books.application.BookInstanceService;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.loans.domain.BookLoan;
import pl.cieszk.libraryapp.features.loans.repository.BookLoanRepository;
import pl.cieszk.libraryapp.features.reservations.application.ReservationService;
import pl.cieszk.libraryapp.features.reservations.domain.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookLoanService {
    private BookLoanRepository bookLoanRepository;
    private BookInstanceService bookInstanceService;
    private ReservationService reservationService;

    private final int MAX_LOANS = 5;

    public BookLoan createLoan(Book book, User user) throws BookNotAvailableException, NoReservationFoundException {
        BookInstance bookInstance;
        try {
            Reservation reservation = reservationService.findReservationByUserAndBook(user, book);
            bookInstance = reservation.getBookInstance();
            reservationService.deleteReservation(reservation.getReservationId());
        } catch (NoReservationFoundException e) {
            bookInstance = bookInstanceService.getAnyAvailable(book);
        }
        BookLoan bookLoan = BookLoan.builder()
                .bookInstance(bookInstance)
                .user(user)
                .loanDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusWeeks(2))
                .build();
        bookLoanRepository.save(bookLoan);
        return bookLoan;
    }

    public BookLoan returnBook(Book book, User user) {
        Optional<BookLoan> bookLoan = bookLoanRepository.findByUserAndBookInstance_Book(user, book);
        if (bookLoan.isPresent()) {
            bookLoan.get().setReturnDate(LocalDateTime.now());
            return bookLoanRepository.save(bookLoan.get());
        } else {
            throw new IllegalArgumentException("Book is not loaned by this user");
        }
    }

    public boolean canUserLoanBook(User user) {
        return bookLoanRepository.findByUser_UserId(user.getUserId()).size() < MAX_LOANS;
    }

    public boolean hasActiveLoan(Book book, User user) {
        return bookLoanRepository.findByUserAndBookInstance_Book(user, book).isPresent();
    }

    public BookLoan renewLoan(Book book, User user) {
        Optional<BookLoan> bookLoan = bookLoanRepository.findByUserAndBookInstance_Book(user, book);
        if (bookLoan.isPresent()) {
            if (bookLoan.get().getRenewCount() < 2) {
                bookLoan.get().setRenewCount(bookLoan.get().getRenewCount() + 1);
                bookLoan.get().setDueDate(bookLoan.get().getDueDate().plusWeeks(2));
                bookLoanRepository.save(bookLoan.get());
            } else {
                throw new IllegalArgumentException("Book cannot be renewed more than twice");
            }
        } else {
            throw new IllegalArgumentException("Book is not loaned by this user");
        }
        return bookLoan.get();
    }

    public List<BookLoan> getCurrentUserLoans(Long userId) {
        return bookLoanRepository.findByUser_UserId(userId);
    }

    public List<BookLoan> getLoanHistory(Long userId) {
        return bookLoanRepository.findByUser_UserIdAndReturnDateIsNotNull(userId);
    }

    public Map<BookInstance, Double> getUserFines(Long userId) {
        return bookLoanRepository.findByUser_UserId(userId).stream()
                .filter(bookLoan -> bookLoan.getFineAmount() > 0)
                .collect(Collectors.groupingBy(BookLoan::getBookInstance, Collectors.summingDouble(BookLoan::getFineAmount)));
    }
}

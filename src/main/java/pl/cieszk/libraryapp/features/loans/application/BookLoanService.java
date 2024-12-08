package pl.cieszk.libraryapp.features.loans.application;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
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
import pl.cieszk.libraryapp.features.loans.application.dto.BookLoanRequestDto;
import pl.cieszk.libraryapp.features.loans.application.dto.BookLoanResponseDto;
import pl.cieszk.libraryapp.features.loans.application.mapper.BookLoanMapper;
import pl.cieszk.libraryapp.features.loans.domain.BookLoan;
import pl.cieszk.libraryapp.features.loans.repository.BookLoanRepository;
import pl.cieszk.libraryapp.features.reservations.application.ReservationService;
import pl.cieszk.libraryapp.features.reservations.application.dto.ReservationResponseDto;
import pl.cieszk.libraryapp.shared.dto.BookUserRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookLoanService {
    private BookLoanRepository bookLoanRepository;
    private BookInstanceService bookInstanceService;
    private ReservationService reservationService;
    private BookLoanMapper bookLoanMapper;
    private BookInstanceMapper bookInstanceMapper;

    public BookLoanService(BookLoanRepository bookLoanRepository, BookInstanceService bookInstanceService, ReservationService reservationService, @Lazy BookLoanMapper bookLoanMapper,@Lazy BookInstanceMapper bookInstanceMapper) {
        this.bookLoanRepository = bookLoanRepository;
        this.bookInstanceService = bookInstanceService;
        this.reservationService = reservationService;
        this.bookLoanMapper = bookLoanMapper;
        this.bookInstanceMapper = bookInstanceMapper;
    }

    private final int MAX_LOANS = 5;

    public BookLoanResponseDto createLoan(BookUserRequest bookUserRequest) throws BookNotAvailableException, NoReservationFoundException {
        BookInstanceResponseDto bookInstance;
        User user = bookUserRequest.toUser();
        Book book = bookUserRequest.toBook();
        try {
            ReservationResponseDto reservation = reservationService.findReservationByUserAndBook(bookUserRequest);
            bookInstance = reservation.getBookInstance();
            reservationService.deleteReservation(bookUserRequest);
        } catch (NoReservationFoundException e) {
            bookInstance = bookInstanceService.getAnyAvailable(book);
        }
        BookLoan bookLoan = BookLoan.builder()
                .bookInstance(bookInstanceMapper.toEntity(bookInstance))
                .user(user)
                .loanDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusWeeks(2))
                .build();
        bookLoanRepository.save(bookLoan);
        return bookLoanMapper.toResponseDto(bookLoan);
    }

    public BookLoanResponseDto returnBook(BookUserRequest bookUserRequest) {
        Optional<BookLoan> bookLoan = bookLoanRepository.findByUserAndBookInstance_Book(bookUserRequest.toUser(), bookUserRequest.toBook());
        if (bookLoan.isPresent()) {
            bookLoan.get().setReturnDate(LocalDateTime.now());
            return bookLoanMapper.toResponseDto(bookLoanRepository.save(bookLoan.get()));
        } else {
            throw new IllegalArgumentException("Book is not loaned by this user");
        }
    }

    public boolean canUserLoanBook(UserRequestDto user) {
        return bookLoanRepository.findByUser_Email(user.getEmail()).size() < MAX_LOANS;
    }

    public boolean hasActiveLoan(BookUserRequest bookUserRequest) {
        return bookLoanRepository.findByUserAndBookInstance_Book(bookUserRequest.toUser(), bookUserRequest.toBook()).isPresent();
    }

    public BookLoanResponseDto renewLoan(BookUserRequest bookUserRequest) {
        Optional<BookLoan> bookLoan = bookLoanRepository.findByUserAndBookInstance_Book(bookUserRequest.toUser(), bookUserRequest.toBook());
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
        return bookLoanMapper.toResponseDto(bookLoan.get());
    }

    public Set<BookLoanResponseDto> getCurrentUserLoans(UserRequestDto user) {
        return bookLoanMapper.toResponseDtos(bookLoanRepository.findByUser_Email(user.getEmail()));
    }

    public Set<BookLoanResponseDto> getLoanHistory(UserRequestDto user) {
        return bookLoanMapper.toResponseDtos(bookLoanRepository.findByUser_EmailAndReturnDateIsNotNull(user.getEmail()));
    }

    public Map<BookInstanceResponseDto, Double> getUserFines(UserRequestDto user) {
        return bookLoanRepository.findByUser_Email(user.getEmail()).stream()
                .filter(bookLoan -> bookLoan.getFineAmount() > 0)
                .collect(Collectors.groupingBy(bookLoan ->
                    bookInstanceMapper.toResponseDto(bookLoan.getBookInstance()),
                    Collectors.summingDouble(BookLoan::getFineAmount)
                ));
    }
}

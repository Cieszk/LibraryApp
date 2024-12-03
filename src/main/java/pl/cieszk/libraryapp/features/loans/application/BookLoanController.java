package pl.cieszk.libraryapp.features.loans.application;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.cieszk.libraryapp.core.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.core.exceptions.custom.NoReservationFoundException;
import pl.cieszk.libraryapp.features.auth.application.dto.UserRequestDto;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceResponseDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookResponseDto;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.loans.application.dto.BookLoanResponseDto;
import pl.cieszk.libraryapp.features.loans.domain.BookLoan;
import pl.cieszk.libraryapp.shared.dto.BookUserRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
@AllArgsConstructor
public class BookLoanController {

    private final BookLoanService bookLoanService;

    @GetMapping("/current")
    public ResponseEntity<Set<BookLoanResponseDto>> getCurrentUserLoans(@RequestBody UserRequestDto user) {
        return ResponseEntity.ok(bookLoanService.getCurrentUserLoans(user));
    }

    @PostMapping("/renew")
    public ResponseEntity<BookLoanResponseDto> renewLoan(@RequestBody BookUserRequest bookUserRequest) {
        BookLoanResponseDto result = bookLoanService.renewLoan(bookUserRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/return")
    public ResponseEntity<BookLoanResponseDto> returnBook(@RequestBody BookUserRequest bookUserRequest) {
        return ResponseEntity.ok(bookLoanService.returnBook(bookUserRequest));
    }

    @PostMapping
    public ResponseEntity<BookLoanResponseDto> createLoan(@RequestBody BookUserRequest bookUserRequest) throws BookNotAvailableException, NoReservationFoundException {
        return ResponseEntity.ok(bookLoanService.createLoan(bookUserRequest));
    }

    @GetMapping("/history")
    public ResponseEntity<Set<BookLoanResponseDto>> getLoanHistory(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(bookLoanService.getLoanHistory(userRequestDto));
    }

    @GetMapping("/fines")
    public ResponseEntity<Map<BookResponseDto, Double>> getUserFines(@RequestBody UserRequestDto userRequestDto) {
        Map<BookInstanceResponseDto, Double> userFines = bookLoanService.getUserFines(userRequestDto);
        return ResponseEntity.ok(userFines.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getBook(), Map.Entry::getValue)));
    }
}

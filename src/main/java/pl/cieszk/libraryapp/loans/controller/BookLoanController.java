package pl.cieszk.libraryapp.loans.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.cieszk.libraryapp.auth.model.User;
import pl.cieszk.libraryapp.books.model.Book;
import pl.cieszk.libraryapp.books.model.BookInstance;
import pl.cieszk.libraryapp.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.loans.dto.BookUserRequest;
import pl.cieszk.libraryapp.loans.model.BookLoan;
import pl.cieszk.libraryapp.loans.service.BookLoanService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
public class BookLoanController {

    private final BookLoanService bookLoanService;

    public BookLoanController(BookLoanService bookLoanService) {
        this.bookLoanService = bookLoanService;
    }

    @GetMapping("/current")
    public ResponseEntity<List<BookLoan>> getCurrentUserLoans(@RequestBody User user) {
        return ResponseEntity.ok(bookLoanService.getCurrentUserLoans(user.getUserId()));
    }

    @PostMapping("/renew")
    public ResponseEntity<BookLoan> renewLoan(@RequestBody Map<String, Object> body) {
        Book book = (Book) body.get("book");
        User user = (User) body.get("user");
        BookLoan result = bookLoanService.renewLoan(book, user);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/return")
    public ResponseEntity<BookLoan> returnBook(@RequestBody BookUserRequest bookUserRequest) {
        Book book = bookUserRequest.getBook();
        User user = bookUserRequest.getUser();
        return ResponseEntity.ok(bookLoanService.returnBook(book, user));
    }

    @PostMapping
    public ResponseEntity<BookLoan> createLoan(@RequestBody BookUserRequest bookUserRequest) throws BookNotAvailableException {
        Book book = bookUserRequest.getBook();
        User user = bookUserRequest.getUser();
        return ResponseEntity.ok(bookLoanService.createLoan(book, user));
    }

    @GetMapping("/history")
    public ResponseEntity<List<BookLoan>> getLoanHistory(@RequestBody User user) {
        return ResponseEntity.ok(bookLoanService.getLoanHistory(user.getUserId()));
    }

    @GetMapping("/fines")
    public ResponseEntity<Map<Book, Double>> getUserFines(@RequestBody User user) {
        Map<BookInstance, Double> userFines = bookLoanService.getUserFines(user.getUserId());
        return ResponseEntity.ok(userFines.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getBook(), Map.Entry::getValue)));
    }
}

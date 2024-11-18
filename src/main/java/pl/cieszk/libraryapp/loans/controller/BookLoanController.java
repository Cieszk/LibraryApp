package pl.cieszk.libraryapp.loans.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cieszk.libraryapp.auth.model.User;
import pl.cieszk.libraryapp.books.model.Book;
import pl.cieszk.libraryapp.books.model.BookInstance;
import pl.cieszk.libraryapp.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.loans.model.BookLoan;
import pl.cieszk.libraryapp.loans.service.BookLoanService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@AllArgsConstructor
public class BookLoanController {

    private final BookLoanService bookLoanService;

    @GetMapping("/current")
    public ResponseEntity<List<BookLoan>> getCurrentUserLoans(@RequestBody User user) {
        return ResponseEntity.ok(bookLoanService.getCurrentUserLoans(user.getUserId()));
    }

    @GetMapping("/renew")
    public ResponseEntity<BookLoan> renewLoan(@RequestBody Book book, @RequestBody User user) {
        return ResponseEntity.ok(bookLoanService.renewLoan(book, user));
    }

    @PostMapping("/return")
    public ResponseEntity<Void> returnBook(@RequestBody Book book, @RequestBody User user) {
        bookLoanService.returnBook(book, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<BookLoan> createLoan(@RequestBody Book book, @RequestBody User user) throws BookNotAvailableException {
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

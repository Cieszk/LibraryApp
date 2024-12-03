package pl.cieszk.libraryapp.features.books.application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.cieszk.libraryapp.features.books.application.dto.BookRequestDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BookResponseDto> addBook(@RequestBody BookRequestDto bookRequestDto) {
        BookResponseDto createdBook = bookService.createBook(bookRequestDto);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<BookResponseDto> updateBook(@RequestBody BookRequestDto bookRequestDto) {
        BookResponseDto updatedBook = bookService.updateBook(bookRequestDto);
        return ResponseEntity.ok(updatedBook);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<Void> deleteBook(@RequestBody BookRequestDto bookRequestDto) {
        System.out.println("User roles: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        bookService.deleteBook(bookRequestDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<BookResponseDto>> getAllBooks(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        List<BookResponseDto> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }
}

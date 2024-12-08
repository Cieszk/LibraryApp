package pl.cieszk.libraryapp.features.authors.application;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.cieszk.libraryapp.features.authors.application.dto.AuthorRequestDto;
import pl.cieszk.libraryapp.features.authors.application.dto.AuthorResponseDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookRequestDto;

import java.util.List;

@Controller
@RequestMapping("/authors")
@AllArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDto> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<AuthorResponseDto>> getAuthorByName(@RequestBody AuthorRequestDto authorRequestDto) {
        return ResponseEntity.ok(authorService.getAuthorByName(authorRequestDto));
    }

    @GetMapping("/book")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<AuthorResponseDto> getAuthorByBook(@RequestBody BookRequestDto bookRequestDto) {
        return ResponseEntity.ok(authorService.getAuthorByBook(bookRequestDto));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDto> createAuthor(@RequestBody AuthorRequestDto authorRequestDto) {
        return ResponseEntity.ok(authorService.createAuthor(authorRequestDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDto> updateAuthor(@PathVariable Long id, @RequestBody AuthorRequestDto authorRequestDto) {
        return ResponseEntity.ok(authorService.updateAuthor(id, authorRequestDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}

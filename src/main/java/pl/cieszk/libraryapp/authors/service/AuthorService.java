package pl.cieszk.libraryapp.authors.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.authors.dto.AuthorDto;
import pl.cieszk.libraryapp.authors.model.Author;
import pl.cieszk.libraryapp.authors.repository.AuthorRepository;
import pl.cieszk.libraryapp.books.model.Book;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorDto getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        return convertToDto(author);
    }

    public Author getAuthorEntityById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));
    }

    private AuthorDto convertToDto(Author author) {
        return AuthorDto.builder()
                .id(author.getAuthorId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .nationality(author.getNationality())
                .biography(author.getBiography())
                .bookIds(author.getBooks().stream().map(Book::getBook_id).collect(Collectors.toSet()))
                .build();
    }
}

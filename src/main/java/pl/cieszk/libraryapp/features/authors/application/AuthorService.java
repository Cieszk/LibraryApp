package pl.cieszk.libraryapp.features.authors.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.features.authors.application.dto.AuthorDto;
import pl.cieszk.libraryapp.features.authors.domain.Author;
import pl.cieszk.libraryapp.features.authors.repository.AuthorRepository;
import pl.cieszk.libraryapp.features.books.domain.Book;

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
                .bookIds(author.getBooks().stream().map(Book::getBookId).collect(Collectors.toSet()))
                .build();
    }
}
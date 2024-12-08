package pl.cieszk.libraryapp.features.authors.application;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.core.exceptions.custom.AuthorNotFoundException;
import pl.cieszk.libraryapp.features.authors.application.dto.AuthorRequestDto;
import pl.cieszk.libraryapp.features.authors.application.dto.AuthorResponseDto;
import pl.cieszk.libraryapp.features.authors.application.mapper.AuthorMapper;
import pl.cieszk.libraryapp.features.authors.domain.Author;
import pl.cieszk.libraryapp.features.authors.repository.AuthorRepository;
import pl.cieszk.libraryapp.features.books.application.dto.BookRequestDto;

import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorService(AuthorRepository authorRepository, @Lazy AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    public AuthorResponseDto getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found"));
        return authorMapper.toResponseDto(author);
    }

    public List<AuthorResponseDto> getAuthorByName(AuthorRequestDto authorRequestDto) {
        List<Author> author = authorRepository.findByFirstNameAndLastName(authorRequestDto.getFirstName(), authorRequestDto.getLastName());
        return authorMapper.toResponseDtos(author);
    }

    public AuthorResponseDto getAuthorByBook(BookRequestDto bookRequestDto) {
        Author author = authorRepository.findByBook(bookRequestDto.getTitle());
        return authorMapper.toResponseDto(author);
    }

    public AuthorResponseDto createAuthor(AuthorRequestDto authorRequestDto) {
        Author author = authorMapper.toEntity(authorRequestDto);
        Author savedAuthor = authorRepository.save(author);
        return authorMapper.toResponseDto(savedAuthor);
    }

    public AuthorResponseDto updateAuthor(Long id, AuthorRequestDto authorRequestDto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found"));
        authorMapper.updateEntityFromDto(authorRequestDto, author);
        Author savedAuthor = authorRepository.save(author);
        return authorMapper.toResponseDto(savedAuthor);
    }

    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found"));
        authorRepository.deleteById(id);
    }

}

package pl.cieszk.libraryapp.features.authors.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cieszk.libraryapp.core.exceptions.custom.AuthorNotFoundException;
import pl.cieszk.libraryapp.features.authors.application.dto.AuthorRequestDto;
import pl.cieszk.libraryapp.features.authors.application.dto.AuthorResponseDto;
import pl.cieszk.libraryapp.features.authors.application.mapper.AuthorMapper;
import pl.cieszk.libraryapp.features.authors.domain.Author;
import pl.cieszk.libraryapp.features.authors.repository.AuthorRepository;
import pl.cieszk.libraryapp.features.books.application.dto.BookRequestDto;
import pl.cieszk.libraryapp.features.books.domain.Book;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private AuthorMapper authorMapper;
    @InjectMocks
    private AuthorService authorService;
    private Author author;
    private AuthorResponseDto authorResponseDto;
    private AuthorRequestDto authorRequestDto;
    private BookRequestDto bookRequestDto;

    @BeforeEach
    void setUp() {
        author = mock(Author.class);
        bookRequestDto = mock(BookRequestDto.class);
        authorResponseDto = mock(AuthorResponseDto.class);
        authorRequestDto = mock(AuthorRequestDto.class);
    }

    @Test
    void getAuthorById_ShouldReturnAuthor() {
        // given
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorMapper.toResponseDto(author)).thenReturn(authorResponseDto);
        // when
        AuthorResponseDto result = authorService.getAuthorById(1L);
        // then
        assertEquals(authorResponseDto, result);
        verify(authorRepository).findById(1L);
    }

    @Test
    void getAuthorById_ShouldThrowAuthorNotFoundException() {
        // given
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(AuthorNotFoundException.class, () -> authorService.getAuthorById(1L));
        verify(authorMapper, never()).toResponseDto(author);
    }

    @Test
    void getAuthorByName_ShouldReturnListOfAuthors() {
        // given
        when(authorRepository.findByFirstNameAndLastName(author.getFirstName(), author.getLastName())).thenReturn(List.of(author));
        when(authorMapper.toResponseDtos(List.of(author))).thenReturn(List.of(authorResponseDto));

        // when
        List<AuthorResponseDto> result = authorService.getAuthorByName(authorRequestDto);

        // then
        assertEquals(result, List.of(authorResponseDto));
        verify(authorRepository).findByFirstNameAndLastName(author.getFirstName(), author.getLastName());
    }

    @Test
    void getAuthorByBook_ShouldReturnAuthor() {
        // given
        when(authorRepository.findByBook(bookRequestDto.getTitle())).thenReturn(author);
        when(authorMapper.toResponseDto(author)).thenReturn(authorResponseDto);

        // when
        AuthorResponseDto result = authorService.getAuthorByBook(bookRequestDto);

        // then
        assertEquals(result, authorResponseDto);
        verify(authorRepository).findByBook(bookRequestDto.getTitle());
    }

    @Test
    void createAuthor_ShouldReturnAuthor() {
        // given
        when(authorMapper.toEntity(authorRequestDto)).thenReturn(author);
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.toResponseDto(author)).thenReturn(authorResponseDto);

        // when
        AuthorResponseDto result = authorService.createAuthor(authorRequestDto);

        // then
        assertEquals(result, authorResponseDto);
        verify(authorRepository, times(1)).save(author);
    }

    @Test
    void updateAuthor_ShouldReturnUpdatedAuthor() {
        // given
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.toResponseDto(author)).thenReturn(authorResponseDto);

        // when
        AuthorResponseDto result = authorService.updateAuthor(1L, authorRequestDto);

        // then
        assertEquals(result, authorResponseDto);
        verify(authorRepository, times(1)).save(author);
    }

    @Test
    void updateAuthor_ShouldThrowAuthorNotFoundException() {
        // given
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(AuthorNotFoundException.class, () -> authorService.updateAuthor(1L, authorRequestDto));
        verify(authorRepository, never()).save(author);
    }

    @Test
    void deleteAuthor_ShouldDeleteAuthor() {
        // given
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        // when
        authorService.deleteAuthor(1L);

        // then
        verify(authorRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteAuthor_ShouldThrowAuthorNotFoundException() {
        // given
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(AuthorNotFoundException.class, () -> authorService.deleteAuthor(1L));
        verify(authorRepository, never()).deleteById(1L);
    }
}

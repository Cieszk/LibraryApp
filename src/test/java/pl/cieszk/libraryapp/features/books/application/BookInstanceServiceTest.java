package pl.cieszk.libraryapp.features.books.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cieszk.libraryapp.core.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceResponseDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookResponseDto;
import pl.cieszk.libraryapp.features.books.application.mapper.BookInstanceMapper;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.books.repository.BookInstanceRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookInstanceServiceTest {

    @Mock
    private BookInstanceRepository bookInstanceRepository;
    @Mock
    private BookInstanceMapper bookInstanceMapper;
    @InjectMocks
    private BookInstanceService bookInstanceService;

    @Test
    void getAnyAvailable_ShouldReturnAvailableBookInstance() throws BookNotAvailableException {
        // Given
        Book book = Book.builder()
                .title("Title")
                .isbn("ISBN")
                .build();

        BookInstance bookInstance = BookInstance.builder()
                .bookInstanceId(1L)
                .book(book)
                .build();

        BookInstanceResponseDto bookInstanceResponseDto = BookInstanceResponseDto.builder()
                .id(1L)
                .book(BookResponseDto.builder()
                        .isbn("ISBN")
                        .title("Title")
                        .build())
                .build();

        when(bookInstanceMapper.toResponseDto(bookInstance)).thenReturn(bookInstanceResponseDto);
        when(bookInstanceRepository.findFirstAvailableByBook(book)).thenReturn(Optional.of(bookInstance));

        when(bookInstanceRepository.findFirstAvailableByBook(bookInstance.getBook())).thenReturn(Optional.of(bookInstance));
        when(bookInstanceService.getAnyAvailable(bookInstance.getBook())).thenReturn(bookInstanceResponseDto);
        // When
        BookInstanceResponseDto result = bookInstanceService.getAnyAvailable(bookInstance.getBook());

        // Then
        assertEquals(result.getBook().getIsbn(), bookInstance.getBook().getIsbn());
    }

    @Test
    void getAnyAvailable_ShouldThrowBookNotAvailableException() {
        // Given
        BookInstance bookInstance = BookInstance.builder()
                .bookInstanceId(1L)
                .build();
        when(bookInstanceRepository.findFirstAvailableByBook(bookInstance.getBook())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotAvailableException.class, () -> bookInstanceService.getAnyAvailable(bookInstance.getBook()));
    }
}

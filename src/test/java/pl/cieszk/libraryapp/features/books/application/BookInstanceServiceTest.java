package pl.cieszk.libraryapp.features.books.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.books.repository.BookInstanceRepository;
import pl.cieszk.libraryapp.core.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.features.books.application.BookInstanceService;

import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookInstanceServiceTest {

    @Mock
    private BookInstanceRepository bookInstanceRepository;
    @InjectMocks
    private BookInstanceService bookInstanceService;

    @Test
    void getAnyAvailable_ShouldReturnAvailableBookInstance() throws BookNotAvailableException {
        // Given
        BookInstance bookInstance = BookInstance.builder()
                .bookInstanceId(1L)
                .build();
        when(bookInstanceRepository.findFirstAvailableByBook(bookInstance.getBook())).thenReturn(Optional.of(bookInstance));

        // When
        Optional<BookInstance> result = bookInstanceService.getAnyAvailable(bookInstance.getBook());

        // Then
        assertTrue(result.isPresent());
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

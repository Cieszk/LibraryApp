package pl.cieszk.libraryapp.books.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cieszk.libraryapp.books.dto.BookDto;
import pl.cieszk.libraryapp.books.model.Book;
import pl.cieszk.libraryapp.books.repository.BookRepository;
import pl.cieszk.libraryapp.exceptions.custom.ResourceAlreadyExistsException;
import pl.cieszk.libraryapp.exceptions.custom.ResourceNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void getAllBooks_ShouldReturnListOfBooks() {
        // Given
        List<Book> mockBooks = new ArrayList<>(Arrays.asList(
                Book.builder()
                        .book_id(1L)
                        .title("Title1")
                        .build(),
                Book.builder()
                        .book_id(2L)
                        .title("Title2")
                        .build()
        ));
        when(bookRepository.findAll()).thenReturn(mockBooks);

        // When
        List<BookDto> result = bookService.getAllBooks();

        // Then
        assertNotNull(result);
        assertEquals(result.size(), mockBooks.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getAllBooks_ShouldReturnEmptyList_WhenThereIsNoBooks() {
        // Given
        List<Book> emptyListMock = Collections.emptyList();
        when(bookRepository.findAll()).thenReturn(emptyListMock);

        // When
        List<BookDto> result = bookService.getAllBooks();

        // Then
        assertNotNull(result);
        assertEquals(result.size(), 0);
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBookById_ShouldReturnBook_WhenBookExists() {
        // Given
        Long bookId = 1L;
        Book mockBook = Book.builder()
                .book_id(bookId)
                .title("Title")
                .isbn("ISBN")
                .build();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));

        // When
        BookDto result = bookService.getBookById(bookId);

        // Then
        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals("Title", result.getTitle());
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void getBookById_ShouldThrowResourceNotFoundException_WhenBookNotFound() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenThrow(new ResourceNotFoundException("Book not found"));

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(bookId));
        verify(bookRepository, times(1)).findById(bookId);

    }

    @Test
    void createBook_ShouldReturnBook_WhenBookExist() {
        // Given
        Book mockBook = Book.builder()
                .title("Title")
                .isbn("ISBN")
                .build();

        BookDto mockBookDto = BookDto.builder()
                .title("Title")
                .isbn("ISBN")
                .build();

        when(bookRepository.existsByIsbn(mockBookDto.getIsbn())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(mockBook);

        // When
        BookDto result = bookService.createBook(mockBookDto);

        // Then
        assertEquals(mockBookDto.getIsbn(), result.getIsbn());
        assertEquals(mockBookDto.getTitle(), result.getTitle());

        verify(bookRepository, times(1)).existsByIsbn(mockBookDto.getIsbn());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBook_ShouldThrowResourceAlreadyExistsException_WhenBookExists() {
        // Given
        BookDto mockBookDto = BookDto.builder()
                .title("Title")
                .isbn("ISBN")
                .build();
        when(bookRepository.existsByIsbn(mockBookDto.getIsbn())).thenThrow(new ResourceAlreadyExistsException("Book with ISBN " + mockBookDto.getIsbn() +  "already exist."));

        // When & Then
        assertThrows(ResourceAlreadyExistsException.class,() -> bookService.createBook(mockBookDto));
        verify(bookRepository, times(1)).existsByIsbn(mockBookDto.getIsbn());
    }

    @Test
    void updateBook_ShouldThrowResourceNotFoundException_WhenBookNotFound() {
        // Given
        Long bookId = 1L;
        BookDto mockBookDto = BookDto.builder()
                .id(bookId)
                .build();
        when(bookRepository.findById(mockBookDto.getId())).thenThrow(new ResourceNotFoundException("Book not found"));

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(bookId ,mockBookDto));
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void updateBook_ShouldReturnUpdatedBook_WhenBookExist() {
        // Given
        Long bookId = 1L;
        BookDto mockBookDto = BookDto.builder()
                .id(bookId)
                .title("updatedTitle")
                .description("updatedDescription")
                .build();

        Book mockBook = Book.builder()
                .book_id(bookId)
                .title("Title")
                .description("Description")
                .build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));
        when(bookRepository.save(any(Book.class))).thenReturn(mockBook);

        // When
        BookDto updatedBook = bookService.updateBook(bookId, mockBookDto);

        // Then
        assertEquals(mockBookDto.getTitle(), updatedBook.getTitle());
        assertEquals(mockBookDto.getDescription(), updatedBook.getDescription());
        assertEquals(mockBookDto.getId(), updatedBook.getId());
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(mockBook);
    }

    @Test
    void deleteBook_shouldDeleteBook_WhenBookExists(){
        // Given
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(true);

        // When
        bookService.deleteBook(bookId);

        // Then
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void deleteBook_ShouldThrowException_WhenBookDoesNotExist() {
        // Given
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(false);

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBook(bookId));
        assertEquals("Book with ID " + bookId + " not found", exception.getMessage());

        // Then
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookRepository, never()).deleteById(bookId);
    }

}

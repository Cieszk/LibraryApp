package pl.cieszk.libraryapp.features.books.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cieszk.libraryapp.core.exceptions.custom.ResourceAlreadyExistsException;
import pl.cieszk.libraryapp.core.exceptions.custom.ResourceNotFoundException;
import pl.cieszk.libraryapp.features.books.application.dto.BookRequestDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookResponseDto;
import pl.cieszk.libraryapp.features.books.application.mapper.BookMapper;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.repository.BookRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private Book book;

    private BookResponseDto bookResponseDto;

    private BookRequestDto bookRequestDto;

    @BeforeEach
    void setUp() {
        bookRequestDto = BookRequestDto.builder()
                .id(1L)
                .title("Title")
                .isbn("ISBN")
                .build();

        book = Book.builder()
                .bookId(1L)
                .title("Title")
                .isbn("ISBN")
                .build();

        bookResponseDto = BookResponseDto.builder()
                .id(1L)
                .title("Title")
                .isbn("ISBN")
                .build();
    }

    @Test
    void getAllBooks_ShouldReturnListOfBooks() {
        // Given
        List<Book> mockBooks = Arrays.asList(book, book, book);
        when(bookRepository.findAll()).thenReturn(mockBooks);
        when(bookMapper.toResponseDto(any(Book.class))).thenReturn(bookResponseDto);

        // When
        List<BookResponseDto> result = bookService.getAllBooks();

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
        List<BookResponseDto> result = bookService.getAllBooks();

        // Then
        assertNotNull(result);
        assertEquals(result.size(), 0);
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBookById_ShouldReturnBook_WhenBookExists() {
        // Given
        Long bookId = book.getBookId();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        // When
        BookResponseDto result = bookService.getBookById(bookId);

        // Then
        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals("Title", result.getTitle());
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void getBookById_ShouldThrowResourceNotFoundException_WhenBookNotFound() {
        // Given
        Long bookId = book.getBookId();
        when(bookRepository.findById(bookId)).thenThrow(new ResourceNotFoundException("Book not found"));

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(bookId));
        verify(bookRepository, times(1)).findById(bookId);

    }

    @Test
    void createBook_ShouldReturnBook_WhenBookExist() {
        // Given
        when(bookRepository.existsByIsbn(bookRequestDto.getIsbn())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toEntity(bookRequestDto)).thenReturn(book);
        when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        // When
        BookResponseDto result = bookService.createBook(bookRequestDto);

        // Then
        assertEquals(bookResponseDto.getIsbn(), result.getIsbn());
        assertEquals(bookResponseDto.getTitle(), result.getTitle());

        verify(bookRepository, times(1)).existsByIsbn(bookResponseDto.getIsbn());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBook_ShouldThrowResourceAlreadyExistsException_WhenBookExists() {
        // Given
        when(bookRepository.existsByIsbn(bookRequestDto.getIsbn())).thenReturn(true);
        when(bookMapper.toEntity(bookRequestDto)).thenReturn(book);

        // When & Then
        assertThrows(ResourceAlreadyExistsException.class,() -> bookService.createBook(bookRequestDto));
        verify(bookRepository, times(1)).existsByIsbn(bookRequestDto.getIsbn());
    }

    @Test
    void updateBook_ShouldThrowResourceNotFoundException_WhenBookNotFound() {
        // Given
        Long bookId = 1L;
        BookRequestDto mockBookDto = BookRequestDto.builder()
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
        Long bookId = book.getBookId();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        // When
        BookResponseDto updatedBook = bookService.updateBook(bookId, bookRequestDto);

        // Then
        assertEquals(bookResponseDto.getTitle(), updatedBook.getTitle());
        assertEquals(bookResponseDto.getDescription(), updatedBook.getDescription());
        assertEquals(bookResponseDto.getId(), updatedBook.getId());
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(book);
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

package pl.cieszk.libraryapp.features.books.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cieszk.libraryapp.core.exceptions.custom.ResourceAlreadyExistsException;
import pl.cieszk.libraryapp.core.exceptions.custom.ResourceNotFoundException;
import pl.cieszk.libraryapp.features.authors.application.mapper.AuthorMapper;
import pl.cieszk.libraryapp.features.books.application.dto.BookRequestDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookResponseDto;
import pl.cieszk.libraryapp.features.books.application.mapper.BookMapper;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.repository.BookRepository;
import pl.cieszk.libraryapp.features.categories.application.mapper.CategoryMapper;
import pl.cieszk.libraryapp.features.publishers.application.mapper.PublisherMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private AuthorMapper authorMapper;

    @Mock
    private PublisherMapper publisherMapper;

    @InjectMocks
    private BookService bookService;

    private Book book;

    private BookResponseDto bookResponseDto;

    private BookRequestDto bookRequestDto;

    @BeforeEach
    void setUp() {
        bookRequestDto = BookRequestDto.builder()
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
        when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
        when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        // When
        BookResponseDto result = bookService.getBookById(bookRequestDto);

        // Then
        assertNotNull(result);
        assertEquals(book.getBookId(), result.getId());
        assertEquals("Title", result.getTitle());
        verify(bookRepository, times(1)).findByIsbn(book.getIsbn());
    }

    @Test
    void getBookById_ShouldThrowResourceNotFoundException_WhenBookNotFound() {
        // Given
        when(bookRepository.findByIsbn(book.getIsbn())).thenThrow(new ResourceNotFoundException("Book not found"));

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(bookRequestDto));
        verify(bookRepository, times(1)).findByIsbn(book.getIsbn());

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
        when(bookRepository.findByIsbn(book.getIsbn())).thenThrow(new ResourceNotFoundException("Book not found"));

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(bookRequestDto));
        verify(bookRepository, times(1)).findByIsbn(book.getIsbn());
    }

    @Test
    void updateBook_ShouldReturnUpdatedBook_WhenBookExist() {
        // Given
        when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
        when(authorMapper.toEntities(bookRequestDto.getAuthors())).thenReturn(Collections.emptySet());
        when(categoryMapper.toEntities(bookRequestDto.getCategories())).thenReturn(Collections.emptySet());
        when(publisherMapper.toEntity(bookRequestDto.getPublisher())).thenReturn(null);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        // When
        BookResponseDto updatedBook = bookService.updateBook(bookRequestDto);

        // Then
        assertEquals(bookResponseDto.getTitle(), updatedBook.getTitle());
        assertEquals(bookResponseDto.getDescription(), updatedBook.getDescription());
        assertEquals(bookResponseDto.getId(), updatedBook.getId());
        verify(bookRepository, times(1)).findByIsbn(book.getIsbn());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void deleteBook_shouldDeleteBook_WhenBookExists(){
        // Given
        String isbn = "ISBN";
        when(bookRepository.existsByIsbn(bookRequestDto.getIsbn())).thenReturn(true);

        // When
        bookService.deleteBook(bookRequestDto);

        // Then
        verify(bookRepository, times(1)).existsByIsbn(isbn);
        verify(bookRepository, times(1)).deleteByIsbn(isbn);
    }

    @Test
    void deleteBook_ShouldThrowException_WhenBookDoesNotExist() {
        // Given
        String isbn = "ISBN";
        when(bookRepository.existsByIsbn(isbn)).thenReturn(false);

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBook(bookRequestDto));
        assertEquals("Book with ISBN " + isbn + " not found", exception.getMessage());

        // Then
        verify(bookRepository, times(1)).existsByIsbn(isbn);
        verify(bookRepository, never()).deleteByIsbn(isbn);
    }

}

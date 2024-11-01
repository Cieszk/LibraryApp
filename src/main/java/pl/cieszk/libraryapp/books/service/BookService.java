package pl.cieszk.libraryapp.books.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.authors.model.Author;
import pl.cieszk.libraryapp.authors.service.AuthorService;
import pl.cieszk.libraryapp.books.dto.BookDto;
import pl.cieszk.libraryapp.books.model.Book;
import pl.cieszk.libraryapp.books.repository.BookRepository;
import pl.cieszk.libraryapp.categories.model.Category;
import pl.cieszk.libraryapp.categories.service.CategoryService;
import pl.cieszk.libraryapp.exceptions.ResourceAlreadyExistsException;
import pl.cieszk.libraryapp.exceptions.ResourceNotFoundException;
import pl.cieszk.libraryapp.publishers.model.Publisher;
import pl.cieszk.libraryapp.publishers.service.PublisherService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final PublisherService publisherService;

    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return convertToDto(book);
    }

    public BookDto createBook(BookDto bookDto) {
        Book book = convertToEntity(bookDto);
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new ResourceAlreadyExistsException("Book with ISBN " + book.getIsbn() + " already exist.");
        }
        book = bookRepository.save(book);
        return convertToDto(book);
    }

    public BookDto updateBook(Long id, BookDto bookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        book.setTitle(bookDto.getTitle());
        book.setGenre(bookDto.getGenre());
        book.setPublishYear(bookDto.getPublishYear());
        book.setIsbn(bookDto.getIsbn());
        book.setLanguage(bookDto.getLanguage());
        book.setPageCount(bookDto.getPageCount());
        book.setDescription(bookDto.getDescription());

        book.setAuthors(fetchAuthorsById(bookDto.getAuthorIds()));
        book.setCategories(fetchCategoriesByIds(bookDto.getCategoryIds()));
        book.setPublisher(fetchPublisherById(bookDto.getPublisherId()));

        book = bookRepository.save(book);
        return convertToDto(book);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book with ID " + id + " not found");
        }
        bookRepository.deleteById(id);
    }

    private Book convertToEntity(BookDto bookDto) {
        Book book = Book.builder()
                .title(bookDto.getTitle())
                .genre(bookDto.getGenre())
                .publishYear(bookDto.getPublishYear())
                .isbn(bookDto.getIsbn())
                .language(bookDto.getLanguage())
                .pageCount(bookDto.getPageCount())
                .description(bookDto.getDescription())
                .build();
        book.setAuthors(fetchAuthorsById(bookDto.getAuthorIds()));
        book.setCategories(fetchCategoriesByIds(bookDto.getCategoryIds()));
        book.setPublisher(fetchPublisherById(bookDto.getPublisherId()));
        return book;
    }


    private BookDto convertToDto(Book book) {
        return BookDto.builder()
                .id(book.getBook_id())
                .title(book.getTitle())
                .genre(book.getGenre())
                .publishYear(book.getPublishYear())
                .isbn(book.getIsbn())
                .language(book.getLanguage())
                .pageCount(book.getPageCount())
                .description(book.getDescription())
                .publisherId(book.getPublisher() != null ? book.getPublisher().getPublisherId() : null)
                .authorIds(book.getAuthors().stream().map(Author::getAuthorId).collect(Collectors.toSet()))
                .categoryIds(book.getCategories().stream().map(Category::getCategoryId).collect(Collectors.toSet()))
                .build();
    }

    private Set<Author> fetchAuthorsById(Set<Long> authorsIds) {
        return authorsIds.stream()
                .map(authorService::getAuthorEntityById)
                .collect(Collectors.toSet());
    }

    private Set<Category> fetchCategoriesByIds(Set<Long> categories) {
        return categories.stream()
                .map(categoryService::findCategoryEntityById)
                .collect(Collectors.toSet());
    }

    private Publisher fetchPublisherById(Long publisherId) {
        return publisherId != null ? publisherService.getPublisherEntityById(publisherId) : null;
    }
}


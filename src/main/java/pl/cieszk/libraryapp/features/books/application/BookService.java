package pl.cieszk.libraryapp.features.books.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.core.exceptions.custom.ResourceAlreadyExistsException;
import pl.cieszk.libraryapp.core.exceptions.custom.ResourceNotFoundException;
import pl.cieszk.libraryapp.features.authors.application.AuthorService;
import pl.cieszk.libraryapp.features.authors.application.mapper.AuthorMapper;
import pl.cieszk.libraryapp.features.authors.domain.Author;
import pl.cieszk.libraryapp.features.books.application.dto.BookRequestDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookResponseDto;
import pl.cieszk.libraryapp.features.books.application.mapper.BookMapper;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.repository.BookRepository;
import pl.cieszk.libraryapp.features.categories.application.CategoryService;
import pl.cieszk.libraryapp.features.categories.application.mapper.CategoryMapper;
import pl.cieszk.libraryapp.features.categories.domain.Category;
import pl.cieszk.libraryapp.features.publishers.application.PublisherService;
import pl.cieszk.libraryapp.features.publishers.application.dto.PublisherResponseDto;
import pl.cieszk.libraryapp.features.publishers.application.mapper.PublisherMapper;
import pl.cieszk.libraryapp.features.publishers.domain.Publisher;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final PublisherService publisherService;
    private final AuthorMapper authorMapper;
    private final CategoryMapper categoryMapper;
    private final PublisherMapper publisherMapper;

    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public BookResponseDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        return bookMapper.toResponseDto(book);
    }

    public BookResponseDto createBook(BookRequestDto bookRequestDto) {
        Book book = bookMapper.toEntity(bookRequestDto);
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new ResourceAlreadyExistsException("Book with ISBN " + book.getIsbn() + " already exist.");
        }
        book = bookRepository.save(book);
        return bookMapper.toResponseDto(book);
    }

    public BookResponseDto updateBook(Long id, BookRequestDto bookRequestDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        bookMapper.updateEntityFromDto(bookRequestDto, book);

        book.setAuthors(fetchAuthorsById(bookRequestDto.getAuthorIds()));
        book.setCategories(fetchCategoriesByIds(bookRequestDto.getCategoryIds()));
        book.setPublisher(fetchPublisherById(bookRequestDto.getPublisherId()));

        book = bookRepository.save(book);
        return bookMapper.toResponseDto(book);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book with ID " + id + " not found");
        }
        bookRepository.deleteById(id);
    }


    private Set<Author> fetchAuthorsById(Set<Long> authorsIds) {
        return authorsIds != null ? authorsIds.stream()
                .map(authorService::getAuthorById)
                .map(authorMapper::toEntity)
                .collect(Collectors.toSet()): Collections.emptySet();
    }

    private Set<Category> fetchCategoriesByIds(Set<Long> categories) {
        return categories != null ? categories.stream()
                .map(categoryService::findCategoryEntityById)
                .map(categoryMapper::toEntity)
                .collect(Collectors.toSet()) : Collections.emptySet();
    }

    private Publisher fetchPublisherById(Long publisherId) {
        return publisherId != null ? publisherMapper.toEntity(publisherService.getPublisherById(publisherId)) : null;
    }
}


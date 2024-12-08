package pl.cieszk.libraryapp.features.books.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final AuthorMapper authorMapper;
    private final CategoryMapper categoryMapper;
    private final PublisherMapper publisherMapper;

    public BookService(BookRepository bookRepository, @Lazy BookMapper bookMapper, @Lazy AuthorMapper authorMapper, @Lazy CategoryMapper categoryMapper, @Lazy PublisherMapper publisherMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.authorMapper = authorMapper;
        this.categoryMapper = categoryMapper;
        this.publisherMapper = publisherMapper;
    }

    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public BookResponseDto getBookById(BookRequestDto bookRequestDto) {
        Book book = bookRepository.findByIsbn(bookRequestDto.getIsbn())
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

    public BookResponseDto updateBook(BookRequestDto bookRequestDto) {
        Book book = bookRepository.findByIsbn(bookRequestDto.getIsbn())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        bookMapper.updateEntityFromDto(bookRequestDto, book);

        book.setAuthors(authorMapper.toEntities(bookRequestDto.getAuthors()));
        book.setCategories(categoryMapper.toEntities(bookRequestDto.getCategories()));
        book.setPublisher(publisherMapper.toEntity(bookRequestDto.getPublisher()));

        book = bookRepository.save(book);
        return bookMapper.toResponseDto(book);
    }

    public void deleteBook(BookRequestDto bookRequestDto) {
        if (!bookRepository.existsByIsbn(bookRequestDto.getIsbn())) {
            throw new ResourceNotFoundException("Book with ISBN " + bookRequestDto.getIsbn() + " not found");
        }
        bookRepository.deleteByIsbn(bookRequestDto.getIsbn());
    }

}


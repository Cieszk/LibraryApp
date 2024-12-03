package pl.cieszk.libraryapp.features.books.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.core.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceResponseDto;
import pl.cieszk.libraryapp.features.books.application.mapper.BookInstanceMapper;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.books.repository.BookInstanceRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BookInstanceService {

    private BookInstanceRepository bookInstanceRepository;
    private BookInstanceMapper bookInstanceMapper;

    public BookInstanceResponseDto getAnyAvailable(Book book) throws BookNotAvailableException {
        Optional<BookInstance> bookInstance = bookInstanceRepository.findFirstAvailableByBook(book);
        if (bookInstance.isPresent()) {
            return bookInstanceMapper.toResponseDto(bookInstance.get());
        }
        throw new BookNotAvailableException("Book is not available");
    }

}

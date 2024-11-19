package pl.cieszk.libraryapp.features.books.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.books.repository.BookInstanceRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BookInstanceService {

    private BookInstanceRepository bookInstanceRepository;

    public Optional<BookInstance> getAnyAvailable(Book book) {
        return bookInstanceRepository.findFirstAvailableByBook(book);
    }

}

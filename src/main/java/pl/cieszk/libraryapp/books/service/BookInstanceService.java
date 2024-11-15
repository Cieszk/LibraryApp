package pl.cieszk.libraryapp.books.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.books.model.Book;
import pl.cieszk.libraryapp.books.model.BookInstance;
import pl.cieszk.libraryapp.books.repository.BookInstanceRepository;
import pl.cieszk.libraryapp.exceptions.custom.BookNotAvailableException;
import pl.cieszk.libraryapp.loans.repository.BookLoanRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BookInstanceService {

    private BookInstanceRepository bookInstanceRepository;

    public Optional<BookInstance> getAnyAvailable(Book book) {
        return bookInstanceRepository.findFirstAvailableByBook(book);
    }

}

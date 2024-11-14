package pl.cieszk.libraryapp.books.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.books.model.Book;
import pl.cieszk.libraryapp.books.model.BookInstance;
import pl.cieszk.libraryapp.books.repository.BookInstanceRepository;
import pl.cieszk.libraryapp.loans.repository.BookLoanRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BookInstanceService {

    private BookInstanceRepository bookInstanceRepository;

    private BookLoanRepository bookLoanRepository;
    public boolean isAvailable(BookInstance bookInstance) {
        boolean hasActiveLoan = bookLoanRepository.findByBookInstanceAndReturnDateIsNull(bookInstance).isPresent();
        boolean hasReservation = bookInstance.getReservation() != null;
        return !hasActiveLoan && !hasReservation;
    }

    public Optional<BookInstance> getAnyAvailable(Book book) {
        return bookInstanceRepository.findFirstAvailableByBook(book);
    }

}

package pl.cieszk.libraryapp.features.loans.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;
import pl.cieszk.libraryapp.features.loans.domain.BookLoan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    List<BookLoan> findByUser_UserId(Long userId);
    List<BookLoan> findByBookInstance_BookInstanceId(Long bookInstanceId);

    Optional<BookLoan> findByBookInstanceAndReturnDateIsNull(BookInstance bookInstance);

    Optional<BookLoan> findByUserAndBookInstance_Book(User user, Book book);

    List<BookLoan> findByUser_UserIdAndReturnDateIsNotNull(Long userId);

    List<BookLoan> findByReturnDateIsNullAndDueDateBefore(LocalDate now);

}

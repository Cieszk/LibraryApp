package pl.cieszk.libraryapp.loans.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.auth.model.User;
import pl.cieszk.libraryapp.books.model.Book;
import pl.cieszk.libraryapp.books.model.BookInstance;
import pl.cieszk.libraryapp.loans.model.BookLoan;

import java.util.List;
import java.util.Optional;

public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    List<BookLoan> findByUser_UserId(Long userId);
    List<BookLoan> findByBookInstance_BookInstanceId(Long bookInstanceId);

    Optional<BookLoan> findByBookInstanceAndReturnDateIsNull(BookInstance bookInstance);

    Optional<BookLoan> findByUserAndBookInstance_Book(User user, Book book);

    List<BookLoan> findByUser_UserIdAndReturnDateIsNotNull(Long userId);

}

package pl.cieszk.libraryapp.loans.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.loans.model.BookLoan;

import java.util.List;

public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    List<BookLoan> findByUser_UserId(Long userId);
    List<BookLoan> findByBookInstance_BookInstanceId(Long bookInstanceId);

}

package pl.cieszk.libraryapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.entity.BookLoan;

import java.util.List;

public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    List<BookLoan> findByUser_UserId(Long userId);
    List<BookLoan> findByBookInstance_BookInstanceId(Long bookInstanceId);

}

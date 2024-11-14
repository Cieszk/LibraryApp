package pl.cieszk.libraryapp.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.cieszk.libraryapp.books.model.Book;
import pl.cieszk.libraryapp.books.model.BookInstance;
import pl.cieszk.libraryapp.books.model.enums.BookStatus;

import java.util.List;
import java.util.Optional;

public interface BookInstanceRepository extends JpaRepository<BookInstance, Long> {
    List<BookInstance> findBookInstanceByBookInstanceId(Long bookInstanceId);
    List<BookInstance> findByBookStatus(BookStatus status);

    @Query("SELECT bi FROM BookInstance bi " +
            "LEFT JOIN bi.bookLoans bl " +
            "WHERE bi.book = :book " +
            "AND bi.bookStatus = 'AVAILABLE' OR bi.bookStatus = 'DAMAGED'" +
            "AND bi.reservation IS NULL " +
            "AND (bl.returnDate IS NOT NULL OR bl IS NULL)")
    Optional<BookInstance> findFirstAvailableByBook(@Param("book") Book book);

}

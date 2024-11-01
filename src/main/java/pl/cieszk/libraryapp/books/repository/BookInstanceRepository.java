package pl.cieszk.libraryapp.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.books.model.BookInstance;
import pl.cieszk.libraryapp.books.model.enums.BookStatus;

import java.util.List;

public interface BookInstanceRepository extends JpaRepository<BookInstance, Long> {
    List<BookInstance> findBookInstanceByBookInstanceId(Long bookInstanceId);
    List<BookInstance> findByBookStatus(BookStatus status);
}

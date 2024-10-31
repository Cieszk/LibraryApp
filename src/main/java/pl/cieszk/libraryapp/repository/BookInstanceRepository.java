package pl.cieszk.libraryapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.entity.BookInstance;
import pl.cieszk.libraryapp.enums.BookStatus;

import java.util.List;

public interface BookInstanceRepository extends JpaRepository<BookInstance, Long> {
    List<BookInstance> findBookInstanceByBookInstanceId(Long bookInstanceId);
    List<BookInstance> findByBookStatus(BookStatus status);
}

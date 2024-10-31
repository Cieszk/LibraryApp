package pl.cieszk.libraryapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.entity.Book;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    Optional<Book> findByTitle(String title);
}

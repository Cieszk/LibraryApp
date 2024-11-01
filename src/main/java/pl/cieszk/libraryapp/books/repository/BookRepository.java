package pl.cieszk.libraryapp.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.books.model.Book;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    Optional<Book> findByTitle(String title);
}

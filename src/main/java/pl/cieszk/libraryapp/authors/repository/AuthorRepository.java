package pl.cieszk.libraryapp.authors.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.authors.model.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}

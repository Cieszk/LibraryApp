package pl.cieszk.libraryapp.features.authors.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.features.authors.domain.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}

package pl.cieszk.libraryapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}

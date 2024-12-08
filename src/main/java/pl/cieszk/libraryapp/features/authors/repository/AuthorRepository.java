package pl.cieszk.libraryapp.features.authors.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.cieszk.libraryapp.features.authors.domain.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    List<Author> findByFirstNameAndLastName(String firstName, String lastName);

    @Query("SELECT a FROM Author a JOIN a.books b WHERE b.title = :title")
    Author findByBook(String title);
}

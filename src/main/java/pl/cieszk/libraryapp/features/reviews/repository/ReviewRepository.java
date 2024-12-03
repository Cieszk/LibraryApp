package pl.cieszk.libraryapp.features.reviews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.reviews.domain.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUser_UserId(Long userId);

    List<Review> findByBook(Book book);

    List<Review> findByBook_Isbn(String isbn);

    Optional<Review> findByBookAndUser(Book book, User user);

    boolean existsByUserAndBook(User user, Book book);

}

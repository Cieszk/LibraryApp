package pl.cieszk.libraryapp.reviews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.reviews.model.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBook_Book_id(Long bookId);
    List<Review> findByUser_UserId(Long userId);
}

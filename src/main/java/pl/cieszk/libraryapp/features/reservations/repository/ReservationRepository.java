package pl.cieszk.libraryapp.features.reservations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.reservations.domain.Reservation;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByBookInstance_BookInstanceId(Long bookInstanceId);
    Optional<Reservation> findByUser_UserId(Long userId);
    Optional<Reservation> findByUserAndBookInstance_Book(User user, Book book);

    boolean existsByUserAndBookInstance_Book(User user, Book book);

    void deleteByUserAndBookInstance_Book(User user, Book book);
    Long countByUser_UserId(Long userId);

    Long countByUser_Email(String email);
}

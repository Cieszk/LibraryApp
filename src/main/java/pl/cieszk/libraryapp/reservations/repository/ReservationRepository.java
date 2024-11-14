package pl.cieszk.libraryapp.reservations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.auth.model.User;
import pl.cieszk.libraryapp.books.model.Book;
import pl.cieszk.libraryapp.books.model.BookInstance;
import pl.cieszk.libraryapp.reservations.model.Reservation;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByBookInstance_BookInstanceId(Long bookInstanceId);
    Optional<Reservation> findByUser_UserId(Long userId);
    Optional<Reservation> findByUserAndBookInstance_Book(User user, Book book);
}

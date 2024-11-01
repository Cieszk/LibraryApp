package pl.cieszk.libraryapp.reservations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.reservations.model.Reservation;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByBookInstance_BookInstanceId(Long bookInstanceId);
    Optional<Reservation> findByUser_UserId(Long userId);
}

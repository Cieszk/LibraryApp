package pl.cieszk.libraryapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.entity.Publisher;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
}

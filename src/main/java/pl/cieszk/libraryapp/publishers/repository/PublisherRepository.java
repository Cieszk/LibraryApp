package pl.cieszk.libraryapp.publishers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.publishers.model.Publisher;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
}

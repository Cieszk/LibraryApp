package pl.cieszk.libraryapp.features.publishers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.features.publishers.domain.Publisher;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
}

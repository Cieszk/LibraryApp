package pl.cieszk.libraryapp.features.publishers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.features.publishers.domain.Publisher;

import java.util.Optional;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    Optional<Publisher> findByName(String name);

    boolean existsByName(String name);

    void deleteByName(String name);
}

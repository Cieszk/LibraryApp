package pl.cieszk.libraryapp.features.categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.features.categories.domain.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}

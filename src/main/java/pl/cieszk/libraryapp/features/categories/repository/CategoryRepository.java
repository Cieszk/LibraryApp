package pl.cieszk.libraryapp.features.categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.features.categories.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

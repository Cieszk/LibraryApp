package pl.cieszk.libraryapp.categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.categories.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

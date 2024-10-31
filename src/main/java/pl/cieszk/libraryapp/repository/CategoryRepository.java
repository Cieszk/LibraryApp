package pl.cieszk.libraryapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cieszk.libraryapp.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

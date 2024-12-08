package pl.cieszk.libraryapp.features.categories.application;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.features.categories.application.dto.CategoryRequestDto;
import pl.cieszk.libraryapp.features.categories.application.dto.CategoryResponseDto;
import pl.cieszk.libraryapp.features.categories.application.mapper.CategoryMapper;
import pl.cieszk.libraryapp.features.categories.domain.Category;
import pl.cieszk.libraryapp.features.categories.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository,@Lazy CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public CategoryResponseDto findCategoryEntityById(CategoryRequestDto categoryRequestDto) {
        Category category = categoryRepository.findByName(categoryRequestDto.getName())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return categoryMapper.toResponseDto(category);
    }
}

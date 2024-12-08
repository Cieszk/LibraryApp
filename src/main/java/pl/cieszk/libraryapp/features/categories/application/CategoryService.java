package pl.cieszk.libraryapp.features.categories.application;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.cieszk.libraryapp.core.exceptions.custom.CategoryNotFoundException;
import pl.cieszk.libraryapp.features.authors.domain.Author;
import pl.cieszk.libraryapp.features.categories.application.dto.CategoryRequestDto;
import pl.cieszk.libraryapp.features.categories.application.dto.CategoryResponseDto;
import pl.cieszk.libraryapp.features.categories.application.mapper.CategoryMapper;
import pl.cieszk.libraryapp.features.categories.domain.Category;
import pl.cieszk.libraryapp.features.categories.repository.CategoryRepository;

import java.util.List;
import java.util.Set;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository,@Lazy CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public CategoryResponseDto findCategoryEntityById(CategoryRequestDto categoryRequestDto) throws CategoryNotFoundException {
        Category category = categoryRepository.findByName(categoryRequestDto.getName())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        return categoryMapper.toResponseDto(category);
    }

    public CategoryResponseDto addCategory(CategoryRequestDto categoryRequestDto) {
        Category category = categoryMapper.toEntity(categoryRequestDto);
        category = categoryRepository.save(category);
        return categoryMapper.toResponseDto(category);
    }

    public CategoryResponseDto updateCategory(Long id,CategoryRequestDto categoryRequestDto) throws CategoryNotFoundException {
        Category category = categoryRepository.findById(id).orElseThrow(() ->
               new CategoryNotFoundException("Category not found.")
        );
        categoryMapper.updateEntityFromDto(categoryRequestDto, category);
        category = categoryRepository.save(category);
        return categoryMapper.toResponseDto(category);
    }

    public void deleteCategory(Long id) throws CategoryNotFoundException {
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                new CategoryNotFoundException("Category not found"));
        categoryRepository.deleteById(id);
    }

    public Set<CategoryResponseDto> getAllCategories() {
        Set<Category> categories = categoryRepository.getAll();
        return categoryMapper.toResponseDtos(categories);
    }
}

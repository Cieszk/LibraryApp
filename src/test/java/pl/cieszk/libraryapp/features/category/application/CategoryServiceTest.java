package pl.cieszk.libraryapp.features.category.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cieszk.libraryapp.core.exceptions.custom.CategoryNotFoundException;
import pl.cieszk.libraryapp.features.categories.application.CategoryService;
import pl.cieszk.libraryapp.features.categories.application.dto.CategoryRequestDto;
import pl.cieszk.libraryapp.features.categories.application.dto.CategoryResponseDto;
import pl.cieszk.libraryapp.features.categories.application.mapper.CategoryMapper;
import pl.cieszk.libraryapp.features.categories.domain.Category;
import pl.cieszk.libraryapp.features.categories.repository.CategoryRepository;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;
    private CategoryRequestDto categoryRequestDto;
    private CategoryResponseDto categoryResponseDto;
    private Category category;

    @BeforeEach
    void setUp() {
        categoryRequestDto = mock(CategoryRequestDto.class);
        categoryResponseDto = mock(CategoryResponseDto.class);
        category = mock(Category.class);
    }

    @Test
    void findCategoryEntityById_ShouldReturnCategoryResponseDto() throws CategoryNotFoundException {
        // given
        when(categoryRepository.findByName(categoryRequestDto.getName())).thenReturn(Optional.of(category));
        when(categoryMapper.toResponseDto(category)).thenReturn(categoryResponseDto);

        // when
        CategoryResponseDto result = categoryService.findCategoryEntityById(categoryRequestDto);

        // then
        assertEquals(result, categoryResponseDto);
        verify(categoryRepository, times(1)).findByName(categoryRequestDto.getName());
        verify(categoryMapper, times(1)).toResponseDto(category);
    }
    @Test
    void findCategoryEntityById_ShouldThrowCategoryNotFoundException() throws CategoryNotFoundException {
        // given
        when(categoryRepository.findByName(categoryRequestDto.getName())).thenReturn(Optional.empty());

        // when
        CategoryNotFoundException ex = assertThrows(CategoryNotFoundException.class, () -> categoryService.findCategoryEntityById(categoryRequestDto));

        // then
        assertEquals(ex.getMessage(), "Category not found");
        verify(categoryRepository, times(1)).findByName(categoryRequestDto.getName());
        verify(categoryMapper, never()).toResponseDto(category);
    }

    @Test
    void addCategory_ShouldCreateCategory() {
        // given
        when(categoryMapper.toEntity(categoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponseDto(category)).thenReturn(categoryResponseDto);

        // when
        CategoryResponseDto result = categoryService.addCategory(categoryRequestDto);

        // then
        assertEquals(result, categoryResponseDto);
        verify(categoryMapper, times(1)).toResponseDto(category);
        verify(categoryMapper, times(1)).toEntity(categoryRequestDto);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void updateCategory_ShouldUpdateCategory() throws CategoryNotFoundException {
        // given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        doNothing().when(categoryMapper).updateEntityFromDto(categoryRequestDto, category);
        when(categoryMapper.toResponseDto(category)).thenReturn(categoryResponseDto);

        // when
        CategoryResponseDto result = categoryService.updateCategory(1L, categoryRequestDto);

        // then
        assertEquals(result, categoryResponseDto);
        verify(categoryMapper, times(1)).toResponseDto(category);
        verify(categoryRepository, times(1)).save(category);
    }


    @Test
    void updateCategory_ShouldShouldThrowCategoryNotFoundException() {
        // given
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        CategoryNotFoundException ex = assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(1L, categoryRequestDto));

        assertEquals(ex.getMessage(), "Category not found.");
        verify(categoryMapper, never()).toResponseDto(category);
        verify(categoryRepository, never()).save(category);
    }

    @Test
    void deleteCategory_ShouldDeleteCategory() throws CategoryNotFoundException {
        // given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).deleteById(1L);

        // when
        categoryService.deleteCategory(1L);

        // then
        verify(categoryRepository, times(1)).deleteById(1L);
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void deleteCategory_ShouldThrowCategoryNotFoundException(){
        // given
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        CategoryNotFoundException ex = assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(1L));

        // then
        assertEquals(ex.getMessage(), "Category not found");
        verify(categoryRepository, never()).deleteById(1L);
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getAllCategories_ShouldReturnSetOfCategories() {
        // given
        when(categoryRepository.getAll()).thenReturn(Set.of(category));
        when(categoryMapper.toResponseDtos(Set.of(category))).thenReturn(Set.of(categoryResponseDto));

        // when
        Set<CategoryResponseDto> result = categoryService.getAllCategories();

        // then
        assertEquals(result, Set.of(categoryResponseDto));
        assertEquals(result.size(), 1);
        verify(categoryRepository, times(1)).getAll();
        verify(categoryMapper, times(1)).toResponseDtos(Set.of(category));

    }
}

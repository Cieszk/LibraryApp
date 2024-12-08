package pl.cieszk.libraryapp.features.categories.application.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import pl.cieszk.libraryapp.features.books.application.mapper.BookMapper;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.categories.application.dto.CategoryRequestDto;
import pl.cieszk.libraryapp.features.categories.application.dto.CategoryResponseDto;
import pl.cieszk.libraryapp.features.categories.domain.Category;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CategoryMapper {
    Category toEntity(CategoryResponseDto categoryResponseDto);
    Category toEntity(CategoryRequestDto categoryRequestDto);
    CategoryResponseDto toResponseDto(Category category);
    Set<Category> toEntities(Set<CategoryRequestDto> categoryRequestDto);
    @Named("MapBooksToIds")
    default Set<Long> mapBooksToIds(Set<Book> books) {
        return books != null ? books.stream()
                .map(Book::getBookId)
                .collect(Collectors.toSet()) : Collections.emptySet();
    }
}

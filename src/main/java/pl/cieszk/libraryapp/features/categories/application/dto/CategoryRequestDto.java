package pl.cieszk.libraryapp.features.categories.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.cieszk.libraryapp.features.books.application.dto.BookRequestDto;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDto {
    private String name;
    private String description;
    private Set<BookRequestDto> books;
}

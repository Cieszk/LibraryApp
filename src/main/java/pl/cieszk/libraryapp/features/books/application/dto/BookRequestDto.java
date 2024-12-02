package pl.cieszk.libraryapp.features.books.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.cieszk.libraryapp.features.authors.application.dto.AuthorRequestDto;
import pl.cieszk.libraryapp.features.authors.application.dto.AuthorResponseDto;
import pl.cieszk.libraryapp.features.categories.application.dto.CategoryRequestDto;
import pl.cieszk.libraryapp.features.publishers.application.dto.PublisherRequestDto;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDto {
    private Long id;
    private String title;
    private String genre;
    private Integer publishYear;
    private String isbn;
    private String language;
    private Integer pageCount;
    private String description;
    private PublisherRequestDto publisher;
    private Set<AuthorRequestDto> authors;
    private Set<CategoryRequestDto> categories;
}

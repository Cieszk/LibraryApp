package pl.cieszk.libraryapp.features.books.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDto {
    private Long id;
    private String title;
    private String genre;
    private Integer publishYear;
    private String isbn;
    private String language;
    private Integer pageCount;
    private String description;
    private Long publisherId;
    private Set<Long> authorIds;
    private Set<Long> categoryIds;
}

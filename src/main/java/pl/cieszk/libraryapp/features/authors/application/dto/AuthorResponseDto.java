package pl.cieszk.libraryapp.features.authors.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.cieszk.libraryapp.features.books.application.dto.BookResponseDto;
import pl.cieszk.libraryapp.features.books.domain.Book;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String nationality;
    private String biography;
    private Set<BookResponseDto> books;
}

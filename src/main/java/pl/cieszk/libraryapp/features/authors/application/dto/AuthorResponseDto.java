package pl.cieszk.libraryapp.features.authors.application.dto;

import pl.cieszk.libraryapp.features.books.application.dto.BookResponseDto;
import pl.cieszk.libraryapp.features.books.domain.Book;

import java.util.Set;

public class AuthorResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String nationality;
    private String biography;
    private Set<BookResponseDto> books;
}

package pl.cieszk.libraryapp.features.authors.application.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import pl.cieszk.libraryapp.features.authors.application.dto.AuthorRequestDto;
import pl.cieszk.libraryapp.features.authors.application.dto.AuthorResponseDto;
import pl.cieszk.libraryapp.features.authors.domain.Author;
import pl.cieszk.libraryapp.features.books.application.mapper.BookMapper;
import pl.cieszk.libraryapp.features.books.domain.Book;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AuthorMapper {

    AuthorResponseDto toResponseDto(Author author);
    Author toEntity(AuthorResponseDto authorResponseDto);
    Author toEntity(AuthorRequestDto authorRequestDto);

    Set<Author> toEntities(Set<AuthorRequestDto> AuthorRequestDto);

    @Named("MapBooksToIds")
    default Set<Long> mapBooksToIds(Set<Book> books) {
        return books != null ? books.stream()
                .map(Book::getBookId)
                .collect(Collectors.toSet()) : Collections.emptySet();
    }

    List<AuthorResponseDto> toResponseDtos(List<Author> author);

    void updateEntityFromDto(AuthorRequestDto authorRequestDto, @MappingTarget Author author);
}

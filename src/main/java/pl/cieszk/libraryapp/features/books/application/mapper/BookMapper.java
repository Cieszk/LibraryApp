package pl.cieszk.libraryapp.features.books.application.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import pl.cieszk.libraryapp.features.authors.application.mapper.AuthorMapper;
import pl.cieszk.libraryapp.features.books.application.dto.BookRequestDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookResponseDto;
import pl.cieszk.libraryapp.features.books.domain.Book;
import pl.cieszk.libraryapp.features.categories.application.mapper.CategoryMapper;
import pl.cieszk.libraryapp.features.publishers.application.mapper.PublisherMapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookMapper {
    void updateEntityFromDto(BookRequestDto bookRequestDto, @MappingTarget Book book);

    BookResponseDto toResponseDto(Book book);
    Book toEntity(BookRequestDto bookRequestDto);
}

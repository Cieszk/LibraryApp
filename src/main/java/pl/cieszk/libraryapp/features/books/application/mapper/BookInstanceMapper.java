package pl.cieszk.libraryapp.features.books.application.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceRequestDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceResponseDto;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;

import java.util.Set;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookInstanceMapper {
    BookInstance toEntity(BookInstanceRequestDto bookInstanceRequestDto);
    BookInstance toEntity(BookInstanceResponseDto bookInstanceResponseDto);
    BookInstanceResponseDto toResponseDto(BookInstance bookInstance);
    Set<BookInstance> toEntities(Set<BookInstanceRequestDto> dtos);

    Set<BookInstanceRequestDto> toRequestDtos(Set<BookInstance> entities);
}

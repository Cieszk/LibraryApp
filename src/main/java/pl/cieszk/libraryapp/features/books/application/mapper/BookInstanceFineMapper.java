package pl.cieszk.libraryapp.features.books.application.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.cieszk.libraryapp.features.books.application.dto.BookInstanceFineDto;
import pl.cieszk.libraryapp.features.books.domain.BookInstance;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {BookInstanceMapper.class})
public interface BookInstanceFineMapper {
    @Mapping(source = "bookInstance", target = "bookInstance")
    @Mapping(source = "fine", target = "fine")
    BookInstanceFineDto toDto(BookInstance bookInstance, Double fine);
}

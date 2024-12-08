package pl.cieszk.libraryapp.features.publishers.application.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import pl.cieszk.libraryapp.features.books.application.mapper.BookMapper;
import pl.cieszk.libraryapp.features.publishers.application.dto.PublisherRequestDto;
import pl.cieszk.libraryapp.features.publishers.application.dto.PublisherResponseDto;
import pl.cieszk.libraryapp.features.publishers.domain.Publisher;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PublisherMapper {
    Publisher toEntity(PublisherResponseDto publisherResponseDto);
    Publisher toEntity(PublisherRequestDto publisherRequestDto);
    PublisherResponseDto toResponseDto(Publisher publisher);
}

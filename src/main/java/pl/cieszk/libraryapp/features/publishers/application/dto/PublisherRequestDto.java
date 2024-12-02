package pl.cieszk.libraryapp.features.publishers.application.dto;

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
public class PublisherRequestDto {
    private Long id;
    private String name;
    private String address;
    private String contactNumber;
    private String website;
    private Set<BookRequestDto> books;
}

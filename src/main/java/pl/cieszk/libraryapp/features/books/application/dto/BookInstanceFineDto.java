package pl.cieszk.libraryapp.features.books.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookInstanceFineDto {
    BookInstanceResponseDto bookInstance;
    Double fine;
}

package pl.cieszk.libraryapp.features.reviews.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.cieszk.libraryapp.features.auth.application.dto.UserRequestDto;
import pl.cieszk.libraryapp.features.books.application.dto.BookRequestDto;
import pl.cieszk.libraryapp.shared.dto.BookUserRequest;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDto {
    private String comment;
    private Integer rating;
    private LocalDateTime reviewDate;
    private BookUserRequest bookUserRequest;
}

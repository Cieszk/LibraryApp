package pl.cieszk.libraryapp.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.books.domain.Book;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookUserRequest {
    private Book book;
    private User user;
}

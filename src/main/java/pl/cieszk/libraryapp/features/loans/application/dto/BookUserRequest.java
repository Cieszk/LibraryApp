package pl.cieszk.libraryapp.features.loans.application.dto;

import lombok.Data;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.books.domain.Book;

@Data
public class BookUserRequest {
    private Book book;
    private User user;
}

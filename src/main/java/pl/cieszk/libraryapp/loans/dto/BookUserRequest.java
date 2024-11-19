package pl.cieszk.libraryapp.loans.dto;

import lombok.Data;
import pl.cieszk.libraryapp.auth.model.User;
import pl.cieszk.libraryapp.books.model.Book;

@Data
public class BookUserRequest {
    private Book book;
    private User user;
}

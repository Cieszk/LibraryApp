package pl.cieszk.libraryapp.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.cieszk.libraryapp.features.auth.application.dto.UserRequestDto;
import pl.cieszk.libraryapp.features.auth.application.mapper.UserMapper;
import pl.cieszk.libraryapp.features.auth.domain.User;
import pl.cieszk.libraryapp.features.books.application.dto.BookRequestDto;
import pl.cieszk.libraryapp.features.books.application.mapper.BookMapper;
import pl.cieszk.libraryapp.features.books.domain.Book;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookUserRequest {
    private BookRequestDto book;
    private UserRequestDto user;
    private BookMapper bookMapper;
    private UserMapper userMapper;

    public Book toBook() {
        return bookMapper.toEntity(book);
    }

    public User toUser() {
        return userMapper.toEntity(user);
    }
}

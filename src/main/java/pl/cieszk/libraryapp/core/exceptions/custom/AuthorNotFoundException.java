package pl.cieszk.libraryapp.core.exceptions.custom;

public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(String authorNotFound) {
        super(authorNotFound);
    }
}

package pl.cieszk.libraryapp.core.exceptions.custom;

public class BookNotAvailableException extends Throwable {
    public BookNotAvailableException(String s) {
        super(s);
    }
}

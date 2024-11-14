package pl.cieszk.libraryapp.exceptions.custom;

public class BookNotAvailableException extends Throwable {
    public BookNotAvailableException(String s) {
        super(s);
    }
}

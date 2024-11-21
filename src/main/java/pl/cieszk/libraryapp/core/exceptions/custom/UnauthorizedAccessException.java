package pl.cieszk.libraryapp.core.exceptions.custom;

public class UnauthorizedAccessException extends Exception {
    public UnauthorizedAccessException(String msg) {
        super(msg);
    }
}

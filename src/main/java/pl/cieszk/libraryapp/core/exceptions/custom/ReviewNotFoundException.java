package pl.cieszk.libraryapp.core.exceptions.custom;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(String msg) {
        super(msg);
    }
}

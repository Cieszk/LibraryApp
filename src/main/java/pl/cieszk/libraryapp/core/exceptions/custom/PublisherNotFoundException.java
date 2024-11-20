package pl.cieszk.libraryapp.core.exceptions.custom;

public class PublisherNotFoundException extends RuntimeException {
    public PublisherNotFoundException(String msg) {
        super(msg);
    }
}

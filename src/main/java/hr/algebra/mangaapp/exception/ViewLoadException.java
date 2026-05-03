package hr.algebra.mangaapp.exception;

public class ViewLoadException extends RuntimeException {

    public ViewLoadException(String message) {
        super(message);
    }

    public ViewLoadException(String message, Throwable cause) {
        super(message, cause);
    }

}

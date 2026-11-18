package shop.exception;

/** Base type for every business failure of the shop (checked). */
public class ShopException extends Exception {
    private static final long serialVersionUID = 1L;

    public ShopException(String message) {
        super(message);
    }
    public ShopException(String message, Throwable cause) {
        super(message, cause);
    }
}
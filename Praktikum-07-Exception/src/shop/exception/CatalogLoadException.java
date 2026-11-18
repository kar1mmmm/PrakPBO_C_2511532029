package shop.exception;

public class CatalogLoadException extends ShopException {
    private static final long serialVersionUID = 1L;

    public CatalogLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}

package shop.exception;

/** Induk semua exception aturan bisnis toko (dari P10). */
public class ShopException extends Exception {
    private static final long serialVersionUID = 1L;

    public ShopException(String message) {
        super(message);
    }
}

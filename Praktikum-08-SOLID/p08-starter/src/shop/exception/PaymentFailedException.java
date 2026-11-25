package shop.exception;

/** Pembayaran gagal karena alasan apa pun. */
public class PaymentFailedException extends ShopException {
    private static final long serialVersionUID = 1L;

    public PaymentFailedException(String message) {
        super(message);
    }
}

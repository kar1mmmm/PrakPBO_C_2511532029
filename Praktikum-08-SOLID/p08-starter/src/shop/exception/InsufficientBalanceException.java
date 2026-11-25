package shop.exception;

/** Saldo e-wallet atau limit kartu tidak cukup. */
public class InsufficientBalanceException extends PaymentFailedException {
    private static final long serialVersionUID = 1L;

    public InsufficientBalanceException(String message) {
        super(message);
    }
}

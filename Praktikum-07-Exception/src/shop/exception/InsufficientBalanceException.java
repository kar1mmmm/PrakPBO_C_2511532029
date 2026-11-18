package shop.exception;

public class InsufficientBalanceException extends PaymentFailedException {
    private static final long serialVersionUID = 1L;

    private final long required;
    private final long available;

    public InsufficientBalanceException(long required, long available) {
        super("Saldo tidak cukup: perlu Rp" + required + ", tersedia Rp" + available);
        this.required = required;
        this.available = available;
    }

    public long getRequired() { return required; }
    public long getAvailable() { return available; }
    public long getShortfall() { return required - available; }
}

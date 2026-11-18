package shop.payment;

public class BankTransferPayment implements PaymentMethod {
    private final String bankName;
    private boolean online = true; // simulation switch

    public BankTransferPayment(String bankName) {
        if (bankName == null || bankName.isBlank()) {
            throw new IllegalArgumentException("Bank name must not be blank");
        }
        this.bankName = bankName;
    }

    @Override
    public String getName() { return "Transfer " + bankName; }

    /** Simulates a bank outage when set to false. */
    public void setOnline(boolean online) { this.online = online; }

    @Override
    public boolean pay(long amount) {
        return amount > 0 && online;
    }
}

package shop.payment;

public class CreditCardPayment implements RefundablePayment, InstallmentPayment {
    private final String cardHolder;
    private final long creditLimit;
    private long usedCredit;

    public CreditCardPayment(String cardHolder, long creditLimit) {
        if (cardHolder == null || cardHolder.isBlank()) {
            throw new IllegalArgumentException("Card holder must not be blank");
        }
        if (creditLimit <= 0) {
            throw new IllegalArgumentException("Credit limit must be positive");
        }
        this.cardHolder = cardHolder;
        this.creditLimit = creditLimit;
    }

    @Override
    public String getName() { return "Kartu Kredit (" + cardHolder + ")"; }

    public long getUsedCredit() { return usedCredit; }

    @Override
    public boolean pay(long amount) {
        if (amount <= 0 || usedCredit + amount > creditLimit) {
            return false;
        }
        usedCredit += amount;
        return true;
    }

    @Override
    public boolean refund(long amount) {
        if (amount <= 0 || amount > usedCredit) {
            return false;
        }
        usedCredit -= amount;
        return true;
    }

    @Override
    public int maxInstallmentMonths() { return 12; }
}

package shop.payment;

import shop.exception.InsufficientBalanceException;
import shop.util.Rupiah;

public class CreditCardPayment implements PaymentMethod {
    private long remainingLimit;

    public CreditCardPayment(long limit) {
        this.remainingLimit = limit;
    }

    @Override
    public String name() {
        return "Kartu Kredit";
    }

    @Override
    public void pay(long amount) throws InsufficientBalanceException {
        if (amount > remainingLimit) {
            throw new InsufficientBalanceException("limit kartu " + Rupiah.format(remainingLimit)
                    + " tidak cukup untuk " + Rupiah.format(amount));
        }
        remainingLimit -= amount;
    }
}

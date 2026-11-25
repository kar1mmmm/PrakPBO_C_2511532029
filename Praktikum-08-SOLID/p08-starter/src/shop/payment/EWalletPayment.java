package shop.payment;

import shop.exception.InsufficientBalanceException;
import shop.util.Rupiah;

public class EWalletPayment implements PaymentMethod {
    private long balance;

    public EWalletPayment(long balance) {
        this.balance = balance;
    }

    @Override
    public String name() {
        return "E-Wallet";
    }

    @Override
    public void pay(long amount) throws InsufficientBalanceException {
        if (amount > balance) {
            throw new InsufficientBalanceException("saldo e-wallet " + Rupiah.format(balance)
                    + " tidak cukup untuk " + Rupiah.format(amount));
        }
        balance -= amount;
    }
}

package shop.payment;

import shop.exception.PaymentFailedException;

public class QrisPayment implements PaymentMethod {
    private static final long MAX_QRIS = 10_000_000L;

    @Override
    public String name() {
        return "QRIS";
    }

    @Override
    public void pay(long amount) throws PaymentFailedException {
        if (amount > MAX_QRIS) {
            throw new PaymentFailedException("transaksi QRIS maksimal Rp10.000.000");
        }
    }
}
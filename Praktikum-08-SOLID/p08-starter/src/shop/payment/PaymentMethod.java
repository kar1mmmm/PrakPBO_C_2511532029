package shop.payment;

import shop.exception.PaymentFailedException;

/** Kontrak semua metode pembayaran (dari P7/P10). */
public interface PaymentMethod {
    String name();

    void pay(long amount) throws PaymentFailedException;
}

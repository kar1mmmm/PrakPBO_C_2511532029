package shop.payment;

import shop.exception.PaymentFailedException;

/** Contract for every payment method. */
public interface PaymentMethod {
    String getName();
    
    /** Completes normally on success; throws when the payment fails. */
    void pay(long amount) throws PaymentFailedException;
}
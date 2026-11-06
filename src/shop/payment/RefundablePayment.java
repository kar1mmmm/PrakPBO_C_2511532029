package shop.payment;

public interface RefundablePayment extends PaymentMethod {
    boolean refund(long amount);
}
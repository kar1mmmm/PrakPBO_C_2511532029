package shop.payment;

public interface PaymentMethod {
    String getName();
    boolean pay(long amount);
}
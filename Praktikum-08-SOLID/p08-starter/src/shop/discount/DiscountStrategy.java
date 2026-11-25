package shop.discount;

public interface DiscountStrategy {
    long discountFor(long subtotal);
}
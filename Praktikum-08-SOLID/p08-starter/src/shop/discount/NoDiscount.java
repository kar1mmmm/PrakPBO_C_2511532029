package shop.discount;

public class NoDiscount implements DiscountStrategy {
    @Override
    public long discountFor(long subtotal) {
        return 0;
    }
}
package shop.discount;

public class PlatinumMemberDiscount implements DiscountStrategy {
    @Override
    public long discountFor(long subtotal) {
        return subtotal * 20 / 100;
    }
}
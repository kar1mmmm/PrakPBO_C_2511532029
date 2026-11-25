package shop.discount;

public class GoldMemberDiscount implements DiscountStrategy {
    private final int percent;

    public GoldMemberDiscount(int percent) {
        this.percent = percent;
    }

    @Override
    public long discountFor(long subtotal) {
        return subtotal * percent / 100;
    }
}
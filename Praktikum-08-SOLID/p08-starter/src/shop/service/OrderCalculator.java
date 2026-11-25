package shop.service;

import shop.discount.DiscountStrategy;
import shop.model.Order;
import shop.model.Shippable;

public class OrderCalculator {
    private final long ratePerKg;

    public OrderCalculator(long ratePerKg) {
        this.ratePerKg = ratePerKg;
    }

    public Bill calculate(Order order, DiscountStrategy discountStrategy) {
        long subtotal = 0;
        int grams = 0;
        for (Order.Line line : order.lines()) {
            subtotal += line.total();
            if (line.product() instanceof Shippable item) {
                grams += item.getWeightGram() * line.qty();
            }
        }

        long discount = discountStrategy.discountFor(subtotal);
        long shipping = grams == 0 ? 0 : ((grams + 999) / 1000) * ratePerKg;
        long total = subtotal - discount + shipping;
        return new Bill(subtotal, discount, shipping, total);
    }
}
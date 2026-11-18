package shop.service;

import java.util.ArrayList;
import java.util.List;
import shop.exception.OutOfStockException;
import shop.exception.PaymentFailedException;
import shop.model.Product;
import shop.payment.PaymentMethod;
import shop.payment.RefundablePayment;
import shop.shipping.Shippable;
import shop.shipping.ShippingService;

public class CheckoutService {

    public long subtotal(List<Product> items) {
        long total = 0;
        for (Product product : items) {
            total += product.getFinalPrice();
        }
        return total;
    }

    public int totalWeight(List<Product> items) {
        int grams = 0;
        for (Product product : items) {
            if (product instanceof Shippable shippable) { // a capability, not a class
                grams += shippable.getWeightGrams();
            }
        }
        return grams;
    }

    public long total(List<Product> items, ShippingService shipping) {
        return subtotal(items) + shipping.calculateCost(totalWeight(items));
    }

    /** Reserves stock, then pays. Returns the amount paid. */
    public long checkout(List<Product> items, ShippingService shipping, PaymentMethod payment)
            throws OutOfStockException, PaymentFailedException {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        List<Product> reserved = new ArrayList<>();
        try {
            for (Product product : items) {
                inventory.reserve(product.getSku(), 1);
                reserved.add(product);
            }
            long amount = total(items, shipping);
            payment.pay(amount);
            return amount;
        } catch (OutOfStockException | PaymentFailedException e) {
            for (Product product : reserved) {
                inventory.release(product.getSku(), 1); // undo the reservation
            }
            throw e;
        }
    }

    public boolean refund(RefundablePayment payment, long amount) {
        return payment.refund(amount);
    }

    private final Inventory inventory;

    public CheckoutService(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory is required");
        }
        this.inventory = inventory;
    }
}

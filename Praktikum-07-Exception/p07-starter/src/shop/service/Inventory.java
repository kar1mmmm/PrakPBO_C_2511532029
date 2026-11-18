package shop.service;

import java.util.HashMap;
import java.util.Map;

/** Stock per SKU. Product objects stay immutable; the stock lives here. */
public class Inventory {
    private final Map<String, Integer> stock = new HashMap<>();

    public void addStock(String sku, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        stock.put(sku, available(sku) + qty);
    }

    public int available(String sku) {
        return stock.getOrDefault(sku, 0);
    }

    /** L3.b: takes qty items out of the stock. */
    public void reserve(String sku, int qty) {
        throw new UnsupportedOperationException("TODO L3.b");
    }

    /** L3.b: puts reserved items back (used to undo a failed checkout). */
    public void release(String sku, int qty) {
        throw new UnsupportedOperationException("TODO L3.b");
    }
}

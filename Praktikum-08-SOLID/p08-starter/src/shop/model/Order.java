package shop.model;

import java.util.ArrayList;
import java.util.List;

/** Pesanan berisi beberapa baris produk. */
public final class Order {

    /** Satu baris pesanan: produk x jumlah. */
    public record Line(Product product, int qty) {
        public Line {
            if (product == null || qty <= 0) {
                throw new IllegalArgumentException("Produk wajib ada dan jumlah harus positif");
            }
        }

        public long total() {
            return Math.multiplyExact(product.getPrice(), qty);
        }
    }

    private final String orderId;
    private final Address address;
    private final List<Line> lines = new ArrayList<>();

    public Order(String orderId, Address address) {
        this.orderId = orderId;
        this.address = address;
    }

    public Order add(Product product, int qty) {
        lines.add(new Line(product, qty));
        return this;
    }

    public List<Line> lines() {
        return List.copyOf(lines);
    }

    public String getOrderId() {
        return orderId;
    }

    public Address getAddress() {
        return address;
    }
}

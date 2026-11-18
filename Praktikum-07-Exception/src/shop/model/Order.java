package shop.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final String orderId;
    private final Address shippingAddress;
    private final List<Line> lines = new ArrayList<>();

    public Order(String orderId, Address shippingAddress) {
        if (orderId == null || orderId.isBlank() || shippingAddress == null) {
            throw new IllegalArgumentException("Order id and address are required");
        }
        this.orderId = orderId;
        this.shippingAddress = shippingAddress;
    }

    public String getOrderId() { return orderId; }
    public Address getShippingAddress() { return shippingAddress; }

    public void addLine(Product product, int quantity) {
        lines.add(new Line(product, quantity));
    }

    public List<Line> getLines() { return Collections.unmodifiableList(lines); }

    public long getItemsTotal() {
        long total = 0;
        for (Line line : lines) {
            total += line.subtotal();
        }
        return total;
    }

    public Receipt createReceipt() {
        return new Receipt();
    }

    /** One line of an order. Static: it does not need the Order object. */
    public static class Line {
        private final Product product;
        private final int quantity;

        public Line(Product product, int quantity) {
            if (product == null || quantity <= 0) {
                throw new IllegalArgumentException("Product is required and quantity must be positive");
            }
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }

        public long subtotal() {
            return product.getFinalPrice() * quantity;
        }
    }

    /** Receipt of one specific order. Inner: it reads the outer Order. */
    public class Receipt {
        public String toText() {
            StringBuilder text = new StringBuilder();
            text.append("STRUK PESANAN ").append(Order.this.orderId).append('\n');
            for (Line line : lines) {
                text.append("- ").append(line.getProduct().getName())
                    .append(" x").append(line.getQuantity())
                    .append(" = Rp").append(line.subtotal()).append('\n');
            }
            text.append("Total barang: Rp").append(getItemsTotal()).append('\n');
            text.append("Kirim ke: ").append(Order.this.shippingAddress);
            return text.toString();
        }
    }
}

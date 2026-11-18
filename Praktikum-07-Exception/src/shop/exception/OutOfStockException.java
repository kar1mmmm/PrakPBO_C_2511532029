package shop.exception;

public class OutOfStockException extends ShopException {
    private static final long serialVersionUID = 1L;
    
    private final String sku;
    private final int requested;
    private final int available;

    public OutOfStockException(String sku, int requested, int available) {
        if (requested <= available) { // Java 25: a check before super(...) (JEP 513)
            throw new IllegalArgumentException("Stock is sufficient, this is not an error");
        }
        super("Stok " + sku + " tidak cukup: diminta " + requested + ", tersedia " + available);
        this.sku = sku;
        this.requested = requested;
        this.available = available;
        }
        public String getSku() { return sku; }
        public int getRequested() { return requested; }
        public int getAvailable() { return available; }
        }
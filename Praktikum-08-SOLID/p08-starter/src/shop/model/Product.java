package shop.model;

/** Produk dasar toko (immutable). */
public abstract class Product {
    private final String sku;
    private final String name;
    private final long price;

    protected Product(String sku, String name, long price) {
        if (sku == null || sku.isBlank() || name == null || name.isBlank()) {
            throw new IllegalArgumentException("SKU dan nama wajib diisi");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Harga harus positif: " + price);
        }
        this.sku = sku;
        this.name = name;
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }
}
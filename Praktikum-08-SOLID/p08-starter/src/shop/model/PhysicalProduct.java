package shop.model;

/** Produk fisik yang dikirim kurir. */
public class PhysicalProduct extends Product implements Shippable {
    private final int weightGram;

    public PhysicalProduct(String sku, String name, long price, int weightGram) {
        super(sku, name, price);
        if (weightGram <= 0) {
            throw new IllegalArgumentException("Berat harus positif: " + weightGram);
        }
        this.weightGram = weightGram;
    }

    @Override
    public int getWeightGram() {
        return weightGram;
    }
}
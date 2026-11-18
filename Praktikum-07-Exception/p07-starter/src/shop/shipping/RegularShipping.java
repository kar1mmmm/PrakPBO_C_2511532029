package shop.shipping;

public class RegularShipping extends ShippingService {
    public RegularShipping() {
        super("Reguler");
    }

    @Override
    protected long ratePerKg() {
        return 10_000;
    }
}

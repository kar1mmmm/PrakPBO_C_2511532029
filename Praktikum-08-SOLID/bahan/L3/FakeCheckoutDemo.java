package shop.testdouble;

import shop.discount.NoDiscount;
import shop.exception.PaymentFailedException;
import shop.model.Address;
import shop.model.Order;
import shop.model.PhysicalProduct;
import shop.service.CheckoutService;
import shop.service.InvoicePrinter;
import shop.service.Notifier;
import shop.service.OrderCalculator;

/**
 * Menguji CheckoutService dengan pembayaran palsu: tanpa saldo, tanpa kartu, tanpa QRIS.
 * Salin file ini ke src/shop/testdouble setelah FakePaymentMethod selesai dibuat.
 */
public class FakeCheckoutDemo {
    public static void main(String[] args) throws PaymentFailedException {
        CheckoutService checkout = new CheckoutService(
                new OrderCalculator(10_000), new InvoicePrinter(), new Notifier());
        Order order = new Order("TEST-1", new Address("Penguji", "Lab"))
                .add(new PhysicalProduct("KOPI", "Kopi Gayo 250g", 85_000, 300), 1);

        FakePaymentMethod fake = new FakePaymentMethod();
        long paid = checkout.checkout(order, new NoDiscount(), fake);

        System.out.println("Fake menerima " + fake.getLastAmount() + ", dipanggil " + fake.getCallCount() + " kali");
        boolean ok = paid == 95_000 && fake.getLastAmount() == 95_000 && fake.getCallCount() == 1;
        System.out.println("Sesuai harapan (95000, 1 kali)? " + ok);
    }
}

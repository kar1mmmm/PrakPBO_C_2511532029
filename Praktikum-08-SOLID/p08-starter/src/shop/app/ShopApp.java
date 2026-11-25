package shop.app;

import shop.discount.GoldMemberDiscount;
import shop.discount.NoDiscount;
import shop.discount.PlatinumMemberDiscount;
import shop.exception.InsufficientBalanceException;
import shop.exception.PaymentFailedException;
import shop.exception.ShopException;
import shop.model.Address;
import shop.model.DigitalProduct;
import shop.model.Order;
import shop.model.PhysicalProduct;
import shop.payment.CreditCardPayment;
import shop.payment.EWalletPayment;
import shop.payment.QrisPayment;
import shop.service.CheckoutService;
import shop.service.InvoicePrinter;
import shop.service.Notifier;
import shop.service.OrderCalculator;
import tools.NimParams;

/** Menjalankan 6 skenario belanja. Output program ini = "golden output". */
public class ShopApp {
    static final PhysicalProduct KOPI = new PhysicalProduct("KOPI", "Kopi Gayo 250g", 85_000, 300);
    static final PhysicalProduct RENDANG = new PhysicalProduct("RDG", "Rendang Kemasan 500g", 120_000, 500);
    static final PhysicalProduct TAS = new PhysicalProduct("TAS", "Tas Anyaman Pandan", 150_000, 800);
    static final DigitalProduct EBOOK = new DigitalProduct("EBK", "E-book Resep Kopi", 45_000,
            "https://toko.example/unduh/ebk");

    static final long CARD_LIMIT = 5_000_000;

public static void main(String[] args) {
    long balance = NimParams.ewalletBalance();
        CheckoutService checkout = new CheckoutService(
        new OrderCalculator(NimParams.ratePerKg()),
        new InvoicePrinter(),
        new Notifier());
            GoldMemberDiscount gold = new GoldMemberDiscount(NimParams.goldPercent());

            run("S1", order("ORD-01", "Sari", "Medan").add(KOPI, 1),
                    o -> checkout.checkout(o, new NoDiscount(), new EWalletPayment(balance)));
            run("S2", order("ORD-02", "Budi", "Padang").add(KOPI, 1).add(RENDANG, 1),
                    o -> checkout.checkout(o, new NoDiscount(), new EWalletPayment(balance)));
            run("S3", order("ORD-03", "Wati", "Bandung").add(TAS, 1).add(KOPI, 2),
                    o -> checkout.checkout(o, gold, new CreditCardPayment(CARD_LIMIT)));
            run("S4", order("ORD-04", "Andi", "Makassar").add(RENDANG, 2),
                    o -> checkout.checkout(o, gold, new EWalletPayment(balance)));
            run("S5", order("ORD-05", "Rina", "Surabaya").add(TAS, 1),
                    o -> checkout.checkout(o, new PlatinumMemberDiscount(), new QrisPayment()));
            run("S6", order("ORD-06", "Dodi", "Jakarta").add(EBOOK, 1).add(KOPI, 1),
                    o -> checkout.checkout(o, new NoDiscount(), new CreditCardPayment(CARD_LIMIT)));
}

    // ---- bagian di bawah ini tidak perlu diubah ----

    interface Step {
        void run(Order order) throws ShopException;
    }

    static Order order(String id, String recipient, String city) {
        return new Order(id, new Address(recipient, city));
    }

    static void run(String scenario, Order order, Step step) {
        System.out.println("=== " + scenario + " " + order.getOrderId() + " ===");
        try {
            step.run(order);
        } catch (InsufficientBalanceException e) {
            System.out.println("[SALDO] " + order.getOrderId() + ": " + e.getMessage());
        } catch (PaymentFailedException e) {
            System.out.println("[BAYAR] " + order.getOrderId() + ": " + e.getMessage());
        } catch (ShopException e) {
            System.out.println("[TOKO] " + order.getOrderId() + ": " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("[INPUT] " + order.getOrderId() + ": " + e.getMessage());
        } catch (RuntimeException e) {
            // Batas program: catat lalu lanjut ke skenario berikutnya (bukan cara menyembunyikan bug).
            System.out.println("[ERROR] " + order.getOrderId() + ": " + e.getMessage());
        }
    }
}

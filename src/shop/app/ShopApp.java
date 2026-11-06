package shop.app;

import java.util.List;
import shop.model.Address;
import shop.model.DigitalProduct;
import shop.model.Order;
import shop.model.PhysicalProduct;
import shop.model.Product;
import shop.payment.BankTransferPayment;
import shop.payment.CreditCardPayment;
import shop.payment.EWalletPayment;
import shop.payment.PaymentMethod;
import shop.service.CheckoutService;
import shop.shipping.ExpressShipping;
import shop.shipping.RegularShipping;
import shop.shipping.ShippingService;

public class ShopApp {
    public static void main(String[] args) {
        // L1: abstract class and abstract method
        Product coffee = new PhysicalProduct("KOPI-001", "Kopi Arabika Gayo 250g", 85_000, 300);
        Product rendang = new PhysicalProduct("RDG-002", "Rendang Kemasan 250g", 120_000, 400);
        Product ebook = new DigitalProduct("EBK-003", "E-book Resep Kue Nusantara", 50_000, "https://toko-umkm.example/unduh/ebk-003");
        List<Product> cart = List.of(coffee, rendang, ebook);

        System.out.println("== Katalog ==");
        for (Product product : cart) {
            System.out.println(product.getInfo());
        }

        // L2: interface and program to an interface
        CheckoutService checkout = new CheckoutService();
        ShippingService regular = new RegularShipping();
        ShippingService express = new ExpressShipping();

        System.out.println("\n== Ringkasan ==");
        System.out.println("Subtotal \t: Rp" + checkout.subtotal(cart));
        System.out.println("Berat kiriman : " + checkout.totalWeight(cart) + " g");
        System.out.println("Total Reguler : Rp" + checkout.total(cart, regular));
        System.out.println("Total Ekspres : Rp" + checkout.total(cart, express));

        // L3: payments
        EWalletPayment wallet = new EWalletPayment("DompetKu", 300_000);
        PaymentMethod transfer = new BankTransferPayment("Bank Nagari");
        
        System.out.println("\n== Pembayaran ==");
        System.out.println(wallet.getName() + " berhasil? " + checkout.checkout(cart, regular, wallet));
        System.out.println("Sisa saldo: Rp" + wallet.getBalance());
        
        System.out.println(transfer.getName() + " berhasil? " + checkout.checkout(cart, express, transfer));

        CreditCardPayment card = new CreditCardPayment("Siti Rahma", 1_000_000);
        System.out.println(card.getName() + " berhasil? " + checkout.checkout(cart, express, card));
        System.out.println("Cicilan 3 bulan: Rp" + card.monthlyInstallment(checkout.total(cart, express), 3));

        System.out.println("\n== Refund e-book ==");
        System.out.println("Refund e-wallet berhasil? " + checkout.refund(wallet, ebook.getFinalPrice()));
        System.out.println("Saldo setelah refund: Rp" + wallet.getBalance());
        
        // checkout.refund(transfer, 1_000); // compile error: BankTransferPayment is not RefundablePayment

        // L4 & L5: Order, Receipt, Anonymous Class
        Address address = new Address("Jl. Moh Hatta No. 10", "Padang", "25121");
        Order order = new Order("ORD-0001", address);
        order.addLine(coffee, 2);
        order.addLine(ebook, 1);
        
        Order.Receipt receipt = order.new Receipt();
        System.out.println("\n" + receipt.toText());

        PaymentMethod voucher = new PaymentMethod() {
            private long remaining = 50_000;

            @Override
            public String getName() {
                return "Voucher Promo Hari Ini";
            }

            @Override
            public boolean pay(long amount) {
                if (amount <= 0 || amount > remaining) {
                    return false;
                }
                remaining -= amount;
                return true;
            }
        };
        
        System.out.println(voucher.getName() + " untuk e-book berhasil? " + checkout.checkout(List.of(ebook), regular, voucher));
    }
}
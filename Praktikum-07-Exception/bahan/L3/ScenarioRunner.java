package shop.app;

import java.util.ArrayList;
import java.util.List;
import shop.StudentInfo;
import shop.model.PhysicalProduct;
import shop.model.Product;
import shop.payment.BankTransferPayment;
import shop.payment.CreditCardPayment;
import shop.payment.EWalletPayment;
import shop.service.CheckoutService;
import shop.service.Inventory;
import shop.shipping.ExpressShipping;
import shop.shipping.RegularShipping;
import shop.shipping.ShippingService;

/**
 * L3: six purchases in a row on THE SAME stock and THE SAME e-wallet.
 * Fill in your predictions on the worksheet before running this program.
 */
public class ScenarioRunner {

    public static void main(String[] args) {
        System.out.println("NIM " + StudentInfo.NIM
                + " | saldo awal Rp" + StudentInfo.walletBalance()
                + " | stok awal KOPI=" + StudentInfo.coffeeStock()
                + " RDG=" + StudentInfo.rendangStock() + " TEH=5");
        for (String line : run(StudentInfo.walletBalance(),
                StudentInfo.coffeeStock(), StudentInfo.rendangStock())) {
            System.out.println(line);
        }
    }

    public static List<String> run(long walletBalance, int coffeeStock, int rendangStock) {
        Product coffee = new PhysicalProduct("KOPI-001", "Kopi Arabika Gayo 250g", 85_000, 300);
        Product rendang = new PhysicalProduct("RDG-002", "Rendang Kemasan 250g", 120_000, 400);
        Product tea = new PhysicalProduct("TEH-004", "Teh Kayu Aro 100g", 35_000, 150);

        Inventory inventory = new Inventory();
        inventory.addStock(coffee.getSku(), coffeeStock);
        inventory.addStock(rendang.getSku(), rendangStock);
        inventory.addStock(tea.getSku(), 5);

        CheckoutService checkout = new CheckoutService(inventory);
        ShippingService regular = new RegularShipping();
        ShippingService express = new ExpressShipping();
        EWalletPayment wallet = new EWalletPayment("DompetKu", walletBalance);
        BankTransferPayment transfer = new BankTransferPayment("Bank Nagari");
        transfer.setOnline(false); // the bank is down today
        CreditCardPayment card = new CreditCardPayment("Siti Rahma", 100_000);

        List<String> out = new ArrayList<>();

        out.add("S1 Kopi + Rendang | Reguler | e-wallet");
        out.add("   " + ShopApp.buy(checkout, List.of(coffee, rendang), regular, wallet));
        out.add(status(inventory, wallet));

        out.add("S2 Kopi x3 | Reguler | transfer bank");
        out.add("   " + ShopApp.buy(checkout, List.of(coffee, coffee, coffee), regular, transfer));
        out.add(status(inventory, wallet));

        out.add("S3 Rendang x2 | Reguler | e-wallet");
        out.add("   " + ShopApp.buy(checkout, List.of(rendang, rendang), regular, wallet));
        out.add(status(inventory, wallet));

        out.add("S4 Kopi | Ekspres | kartu kredit (limit Rp100000)");
        out.add("   " + ShopApp.buy(checkout, List.of(coffee), express, card));
        out.add(status(inventory, wallet));

        out.add("S5 Keranjang kosong | Reguler | e-wallet");
        try {
            out.add("   " + ShopApp.buy(checkout, List.of(), regular, wallet));
        } catch (IllegalStateException e) {
            out.add("   [INVALID] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        out.add(status(inventory, wallet));

        out.add("S6 Teh x2 | Reguler | e-wallet");
        out.add("   " + ShopApp.buy(checkout, List.of(tea, tea), regular, wallet));
        out.add(status(inventory, wallet));

        return out;
    }

    private static String status(Inventory inventory, EWalletPayment wallet) {
        return "   -> stok KOPI=" + inventory.available("KOPI-001")
                + " RDG=" + inventory.available("RDG-002")
                + " TEH=" + inventory.available("TEH-004")
                + " | saldo Rp" + wallet.getBalance();
    }
}

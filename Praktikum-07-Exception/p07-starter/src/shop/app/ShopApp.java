package shop.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import shop.StudentInfo;
import shop.model.PhysicalProduct;
import shop.model.Product;
import shop.payment.EWalletPayment;
import shop.service.CheckoutService;
import shop.shipping.RegularShipping;
import shop.shipping.ShippingService;

public class ShopApp {

    public static void main(String[] args) {
        List<Product> catalog = sampleCatalog(); // L4: replaced by CatalogReader

        System.out.println("== Toko Online UMKM ==");
        for (int i = 0; i < catalog.size(); i++){
            System.out.println((i + 1) + ". " + catalog.get(i).getInfo());
        }

        List<Product> cart = new ArrayList<>();
        try (Scanner in = new Scanner(System.in)) { // closed automatically (see L4)
            while (true) {
                System.out.print("Nomor produk (0 = selesai): ");
                int choice = Integer.parseInt(in.nextLine().trim());
                if (choice == 0) {
                    break;
                }
                if (choice < 1 || choice > catalog.size()) {
                    System.out.println("Nomor produk tidak ada.");
                    continue;
                }
                Product product = catalog.get(choice - 1);

                System.out.print("Jumlah: ");
                int quantity;
                try {
                    quantity = Integer.parseInt(in.nextLine().trim());
                } catch (NumberFormatException _) {
                    System.out.println("Input harus berupa angka.");
                    continue;
                }
                for (int i = 0; i < quantity; i++) {
                    cart.add(product);
                }

                if (quantity <= 0) {
                    System.out.println("Jumlah harus lebih dari 0.");
                } else {
                    System.out.println("Menambahkan " + quantity + " x " + product.getName() + " ke keranjang.");
                }
                System.out.println("  + " + quantity + " x " + product.getName());
            }
        }

        CheckoutService checkout = new CheckoutService();
        ShippingService regular = new RegularShipping();
        EWalletPayment wallet = new EWalletPayment("DompetKu", StudentInfo.walletBalance());
        System.out.println("\nKeranjang: " + cart.size() + " barang, total Reguler Rp"
                + checkout.total(cart, regular));
        boolean paid = checkout.checkout(cart, regular, wallet);
        System.out.println("Pembayaran berhasil? " + paid);
        System.out.println("Sisa saldo: Rp" + wallet.getBalance());
    }

    private static List<Product> sampleCatalog() {
        return List.of(
                new PhysicalProduct("KOPI-001", "Kopi Arabika Gayo 250g", 85_000, 300),
                new PhysicalProduct("RDG-002", "Rendang Kemasan 250g", 120_000, 400),
                new PhysicalProduct("TEH-004", "Teh Kayu Aro 100g", 35_000, 150));
    }
}

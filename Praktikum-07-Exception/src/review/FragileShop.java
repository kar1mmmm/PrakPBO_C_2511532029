package review;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import shop.exception.CatalogLoadException;
import shop.exception.OutOfStockException;
import shop.exception.PaymentFailedException;
import shop.payment.EWalletPayment;
import shop.payment.PaymentMethod;
import shop.service.Inventory;

/** L5: corrected exception handling examples. */
public class FragileShop {
    public static void payOrder(PaymentMethod payment, long amount)
            throws PaymentFailedException {
        payment.pay(amount);
    }

    public static boolean isInStock(Inventory inventory, String sku) {
        return inventory.available(sku) > 0;
    }

    public static String readCatalogHeader(Path file) throws CatalogLoadException {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            return reader.readLine();
        } catch (IOException e) {
            throw new CatalogLoadException("Katalog gagal dibaca: " + file, e);
        }
    }

    public static String addToCart(Inventory inventory, String sku, String quantityText) {
        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            return "Gagal: jumlah harus berupa angka.";
        }
        if (quantity <= 0) {
            return "Gagal: jumlah harus lebih dari 0.";
        }
        try {
            inventory.reserve(sku, quantity);
            return "Ditambahkan " + quantity + " x " + sku;
        } catch (OutOfStockException e) {
            return "Gagal: stok " + sku + " tidak cukup, tersedia " + e.getAvailable() + ".";
        }
    }

    public static void main(String[] args) {
        EWalletPayment wallet = new EWalletPayment("DompetKu", 50_000);
        try {
            payOrder(wallet, 85_000);
            System.out.println("1) Bayar Rp85000 selesai. Sisa saldo: Rp" + wallet.getBalance());
        } catch (PaymentFailedException e) {
            System.out.println("1) Pembayaran gagal: " + e.getMessage());
        }
        Inventory inventory = new Inventory();
        inventory.addStock("KOPI-001", 1);
        System.out.println("2) Kopi tersedia? " + isInStock(inventory, "KOPI-001"));
        System.out.println("3) " + addToCart(inventory, "KOPI-001", "dua"));
        System.out.println("   " + addToCart(inventory, "KOPI-001", "5"));
        try {
            System.out.println("4) " + readCatalogHeader(Path.of("katalog-lama.csv")));
        } catch (CatalogLoadException e) {
            System.out.println("4) " + e.getMessage() + " | penyebab: " + e.getCause());
        }
    }
}

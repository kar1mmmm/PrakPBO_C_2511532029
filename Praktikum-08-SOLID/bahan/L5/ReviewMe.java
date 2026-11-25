package review;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * L5 Code review. Baca setiap POTONG, lalu isi tabel di LK:
 * prinsip yang dilanggar (atau "sudah baik"), dampaknya, dan sketsa perbaikan.
 * File ini tidak perlu dijalankan, cukup dibaca (tetapi bisa dikompilasi).
 */
public class ReviewMe {

    // ===================== POTONG 1 =====================
    static class SalesReport {
        private final List<Long> sales = new ArrayList<>();

        void add(long amount) { sales.add(amount); }

        long total() { return sales.stream().mapToLong(Long::longValue).sum(); }

        void saveCsv(Path file) throws IOException {
            Files.writeString(file, "total;" + total());
        }

        void emailToOwner(String address) {
            System.out.println("Kirim email ke " + address + ": total " + total());
        }
    }

    // ===================== POTONG 2 =====================
    static class ShippingCostCalculator {
        long cost(String courier, int kg) {
            if (courier.equals("JNE")) {
                return kg * 9_000L;
            } else if (courier.equals("JNT")) {
                return kg * 8_500L;
            } else if (courier.equals("SICEPAT")) {
                return 5_000L + kg * 7_000L;
            }
            throw new IllegalArgumentException("Kurir tidak dikenal: " + courier);
        }
    }

    // ===================== POTONG 3 =====================
    static class Cart {
        protected final List<String> items = new ArrayList<>();

        void addItem(String sku) { items.add(sku); }

        int size() { return items.size(); }
    }

    /** Keranjang milik pesanan yang sudah dibayar: tidak boleh diubah lagi. */
    static class ReadOnlyCart extends Cart {
        @Override
        void addItem(String sku) {
            throw new UnsupportedOperationException("Keranjang sudah dikunci");
        }
    }

    // ===================== POTONG 4 =====================
    static class EWalletPay {
        boolean pay(long amount) { return amount <= 500_000; }
    }

    static class OrderService {
        void placeOrder(String orderId, long amount) {
            EWalletPay wallet = new EWalletPay();
            if (wallet.pay(amount)) {
                System.out.println(orderId + " dibayar");
            }
        }
    }

    // ===================== POTONG 5 =====================
    static final class PriceFormatter {
        private PriceFormatter() { }

        static String format(long rupiah) {
            return "Rp" + String.format(Locale.ROOT, "%,d", rupiah).replace(',', '.');
        }
    }
}

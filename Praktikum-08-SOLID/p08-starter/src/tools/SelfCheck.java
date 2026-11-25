package tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * SelfCheck Praktikum P08 (SOLID). Jangan diubah.
 * 8 behavior check (perilaku tidak boleh berubah) + 12 design check (struktur
 * kode).
 * Jalankan dari folder proyek (folder yang berisi src).
 */
public final class SelfCheck {
    private static int passed = 0;
    private static int total = 0;
    private static final Path SRC = Path.of("src");

    private SelfCheck() {
    }

    public static void main(String[] args) {
        System.out.println("=== SelfCheck P08 SOLID ===");
        System.out.println("Nama : " + StudentInfo.NAME + " | NIM: " + StudentInfo.NIM
                + " | Kelas: " + StudentInfo.CLASS_NAME);
        int n;
        try {
            n = NimParams.lastTwo();
        } catch (IllegalStateException e) {
            System.out.println("[STOP] " + e.getMessage());
            return;
        }
        System.out.println("Parameter NIM (" + String.format("%02d", n) + "): saldo e-wallet "
                + rp(NimParams.ewalletBalance()) + ", diskon GOLD " + NimParams.goldPercent()
                + "%, ongkir " + rp(NimParams.ratePerKg()) + "/kg");
        if (!Files.isDirectory(SRC)) {
            System.out.println("[PERINGATAN] Folder src tidak ditemukan dari " + Path.of("").toAbsolutePath()
                    + ". Design check yang membaca source akan gagal.");
        }

        System.out.println();
        System.out.println("--- Behavior check (perilaku harus tetap sama) ---");
        behaviorChecks();

        System.out.println();
        System.out.println("--- Design check (struktur kode) ---");
        designChecks();

        System.out.println();
        System.out.printf(Locale.ROOT, "Skor SelfCheck: %d/%d  -> nilai %.1f dari 50%n",
                passed, total, passed * 50.0 / total);

        System.out.println();
        System.out.println("--- Tantangan (tidak dihitung di skor di atas) ---");
        bonusChecks();
    }

    // ================= BEHAVIOR =================

    private static void behaviorChecks() {
        List<String> actual;
        try {
            actual = runShopApp();
        } catch (ReflectiveOperationException | RuntimeException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            System.out.println("[STOP] ShopApp tidak bisa dijalankan: " + cause);
            for (int i = 1; i <= 8; i++) {
                check("B" + i + " (ShopApp gagal dijalankan)", false, null);
            }
            return;
        }
        Expected exp = Expected.build();
        String[] titles = {
                "S1 e-wallet, member biasa",
                "S2 e-wallet, dua produk",
                "S3 kartu kredit, member GOLD",
                "S4 e-wallet, member GOLD",
                "S5 QRIS, member PLATINUM",
                "S6 pesanan berisi produk digital",
        };
        for (int i = 0; i < 6; i++) {
            String id = "S" + (i + 1);
            List<String> e = exp.block(id);
            List<String> a = block(actual, id);
            String hint = firstDiff(e, a);
            check("B" + (i + 1) + " " + titles[i] + " sesuai golden output", hint == null, hint);
        }
        String hint7 = firstDiff(exp.all, actual);
        check("B7 seluruh output ShopApp identik (format invoice tidak berubah)", hint7 == null, hint7);

        long sms = actual.stream().filter(l -> l.startsWith("[SMS] ")).count();
        boolean ok8 = sms == exp.successCount;
        for (String id : exp.successIds) {
            long c = actual.stream().filter(l -> l.startsWith("[SMS] ") && l.contains(" " + id + " ")).count();
            ok8 &= c == 1;
        }
        check("B8 notifikasi [SMS] terkirim tepat 1 kali untuk setiap pesanan yang berhasil", ok8,
                "ditemukan " + sms + " baris [SMS], seharusnya " + exp.successCount);
    }

    private static List<String> runShopApp() throws ReflectiveOperationException {
        Class<?> app = Class.forName("shop.app.ShopApp");
        Method main = app.getMethod("main", String[].class);
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream capture = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            main.invoke(null, (Object) new String[0]);
        } finally {
            System.setOut(original);
        }
        List<String> lines = new ArrayList<>();
        for (String l : buffer.toString(StandardCharsets.UTF_8).split("\\R")) {
            if (!l.isBlank()) {
                lines.add(l.stripTrailing());
            }
        }
        return lines;
    }

    private static List<String> block(List<String> lines, String scenario) {
        List<String> result = new ArrayList<>();
        boolean inside = false;
        for (String l : lines) {
            if (l.startsWith("=== ")) {
                inside = l.startsWith("=== " + scenario + " ");
            }
            if (inside) {
                result.add(l);
            }
        }
        return result;
    }

    private static String firstDiff(List<String> expected, List<String> actual) {
        int max = Math.max(expected.size(), actual.size());
        for (int i = 0; i < max; i++) {
            String e = i < expected.size() ? expected.get(i) : "(tidak ada baris)";
            String a = i < actual.size() ? actual.get(i) : "(tidak ada baris)";
            if (!e.equals(a)) {
                return "baris " + (i + 1) + "\n           diharapkan: " + e + "\n           tercetak  : " + a;
            }
        }
        return null;
    }

    /** Golden output dihitung ulang secara mandiri dari parameter NIM. */
    private static final class Expected {
        final List<String> all = new ArrayList<>();
        final List<String> successIds = new ArrayList<>();
        int successCount;

        record Item(String name, long price, int grams) {
        }

        static final Item KOPI = new Item("Kopi Gayo 250g", 85_000, 300);
        static final Item RDG = new Item("Rendang Kemasan 500g", 120_000, 500);
        static final Item TAS = new Item("Tas Anyaman Pandan", 150_000, 800);
        static final Item EBK = new Item("E-book Resep Kopi", 45_000, 0);

        static Expected build() {
            Expected x = new Expected();
            long bal = NimParams.ewalletBalance();
            x.add("S1", "ORD-01", "Sari", "Medan", 0, "EWALLET", bal, new Object[] { KOPI, 1 });
            x.add("S2", "ORD-02", "Budi", "Padang", 0, "EWALLET", bal, new Object[] { KOPI, 1, RDG, 1 });
            x.add("S3", "ORD-03", "Wati", "Bandung", NimParams.goldPercent(), "CARD", 5_000_000,
                    new Object[] { TAS, 1, KOPI, 2 });
            x.add("S4", "ORD-04", "Andi", "Makassar", NimParams.goldPercent(), "EWALLET", bal,
                    new Object[] { RDG, 2 });
            x.add("S5", "ORD-05", "Rina", "Surabaya", 20, "QRIS", 0, new Object[] { TAS, 1 });
            x.add("S6", "ORD-06", "Dodi", "Jakarta", 0, "CARD", 5_000_000, new Object[] { EBK, 1, KOPI, 1 });
            return x;
        }

        void add(String sc, String id, String who, String city, int pct, String pay, long funds,
                Object[] items) {
            all.add("=== " + sc + " " + id + " ===");
            long sub = 0;
            int grams = 0;
            List<String> itemLines = new ArrayList<>();
            for (int i = 0; i < items.length; i += 2) {
                Item it = (Item) items[i];
                int q = (Integer) items[i + 1];
                sub += it.price * q;
                grams += it.grams * q;
                itemLines.add(it.name + " x" + q + " = " + rp(it.price * q));
            }
            long disc = sub * pct / 100;
            long ship = grams == 0 ? 0 : ((grams + 999) / 1000) * NimParams.ratePerKg();
            long tot = sub - disc + ship;
            String payName;
            switch (pay) {
                case "EWALLET" -> {
                    payName = "E-Wallet";
                    if (tot > funds) {
                        all.add("[SALDO] " + id + ": saldo e-wallet " + rp(funds) + " tidak cukup untuk " + rp(tot));
                        return;
                    }
                }
                case "CARD" -> payName = "Kartu Kredit";
                default -> payName = "QRIS";
            }
            all.add("--- INVOICE " + id + " ---");
            all.add("Kirim ke : " + who + ", " + city);
            all.addAll(itemLines);
            all.add("Subtotal : " + rp(sub));
            all.add("Diskon   : " + rp(disc));
            all.add("Ongkir   : " + rp(ship));
            all.add("Total    : " + rp(tot));
            all.add("Bayar    : " + payName);
            all.add("[SMS] " + who + ": pesanan " + id + " sudah dibayar (" + rp(tot) + ")");
            successCount++;
            successIds.add(id);
        }

        List<String> block(String sc) {
            return SelfCheck.block(all, sc);
        }
    }

    // ================= DESIGN =================

    private static void designChecks() {
        check("D1 class OrderProcessor (God class) sudah tidak ada", find("shop.service.OrderProcessor") == null,
                "pecah tanggung jawabnya, lalu ganti nama menjadi CheckoutService (L2)");
        check("D2 class shop.service.OrderCalculator ada", isClass("shop.service.OrderCalculator"), "lihat L2");
        check("D3 class shop.service.InvoicePrinter ada", isClass("shop.service.InvoicePrinter"), "lihat L2");
        check("D4 class shop.service.Notifier ada", isClass("shop.service.Notifier"), "lihat L2");

        Class<?> cs = find("shop.service.CheckoutService");
        boolean d5 = false;
        if (cs != null) {
            for (Constructor<?> c : cs.getConstructors()) {
                List<String> names = Arrays.stream(c.getParameterTypes()).map(Class::getName).toList();
                d5 |= names.contains("shop.service.OrderCalculator")
                        && names.contains("shop.service.InvoicePrinter")
                        && names.contains("shop.service.Notifier");
            }
        }
        check("D5 CheckoutService menerima OrderCalculator, InvoicePrinter, Notifier lewat constructor", d5,
                "public CheckoutService(OrderCalculator, InvoicePrinter, Notifier)");

        String src = source("shop/service/CheckoutService.java");
        boolean d6 = src != null && !src.contains("switch")
                && Stream.of("\"EWALLET\"", "\"CARD\"", "\"QRIS\"", "\"GOLD\"", "\"PLATINUM\"",
                        "new EWalletPayment", "new CreditCardPayment", "new QrisPayment")
                        .noneMatch(src::contains);
        check("D6 CheckoutService tanpa switch, kode teks, dan new untuk class pembayaran", d6,
                src == null ? "file src/shop/service/CheckoutService.java tidak ditemukan"
                        : "pindahkan pemilihan pembayaran/diskon ke ShopApp (L3)");

        Class<?> ds = find("shop.discount.DiscountStrategy");
        check("D7 interface shop.discount.DiscountStrategy ada", ds != null && ds.isInterface(), "lihat L3");
        boolean d8 = ds != null;
        for (String impl : List.of("NoDiscount", "GoldMemberDiscount", "PlatinumMemberDiscount")) {
            Class<?> c = find("shop.discount." + impl);
            d8 &= c != null && ds.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers());
        }
        check("D8 NoDiscount, GoldMemberDiscount, PlatinumMemberDiscount mengimplementasikan DiscountStrategy",
                d8, "lihat L3");

        Class<?> pm = find("shop.payment.PaymentMethod");
        Class<?> qris = find("shop.payment.QrisPayment");
        check("D9 shop.payment.QrisPayment mengimplementasikan PaymentMethod",
                pm != null && qris != null && pm.isAssignableFrom(qris), "lihat L3");

        boolean d10 = false;
        String why10 = "buat shop.testdouble.FakePaymentMethod (L3)";
        Class<?> fake = find("shop.testdouble.FakePaymentMethod");
        if (fake != null && pm != null && pm.isAssignableFrom(fake)) {
            try {
                Object f = fake.getConstructor().newInstance();
                fake.getMethod("pay", long.class).invoke(f, 12_345L);
                Object last = fake.getMethod("getLastAmount").invoke(f);
                d10 = Long.valueOf(12_345L).equals(last);
                why10 = "getLastAmount() harus mengembalikan jumlah pada pemanggilan pay() terakhir";
            } catch (ReflectiveOperationException e) {
                why10 = "perlu constructor tanpa parameter dan method long getLastAmount(): " + e;
            }
        }
        check("D10 FakePaymentMethod (test double) mencatat jumlah yang dibayar", d10, why10);

        Class<?> ship = find("shop.model.Shippable");
        Class<?> down = find("shop.model.Downloadable");
        Class<?> phys = find("shop.model.PhysicalProduct");
        Class<?> dig = find("shop.model.DigitalProduct");
        boolean d11 = ship != null && down != null && ship.isInterface() && down.isInterface()
                && phys != null && dig != null && ship.isAssignableFrom(phys) && down.isAssignableFrom(dig);
        check("D11 interface Shippable dan Downloadable; PhysicalProduct -> Shippable, DigitalProduct -> Downloadable",
                d11, "lihat L4");

        boolean d12 = ship != null && dig != null && !ship.isAssignableFrom(dig);
        String why12 = "DigitalProduct tidak boleh menjadi Shippable (L4)";
        if (d12) {
            String model = sourcesIn("shop/model");
            d12 = model != null && !model.contains("UnsupportedOperationException");
            why12 = "masih ada UnsupportedOperationException di package shop.model";
        }
        check("D12 DigitalProduct bukan Shippable dan tidak ada method yang 'menolak' dipanggil", d12, why12);
    }

    // ================= BONUS =================

    private static void bonusChecks() {
        Class<?> rate = find("shop.shipping.ShippingRate");
        Class<?> sameDay = find("shop.shipping.SameDayRate");
        Class<?> calc = find("shop.service.OrderCalculator");
        boolean a = rate != null && rate.isInterface() && sameDay != null && rate.isAssignableFrom(sameDay)
                && calc != null && hasConstructorWith(calc, rate);
        System.out.println((a ? "[TERDETEKSI] " : "[BELUM]      ")
                + "Tantangan A: ShippingRate + SameDayRate, OrderCalculator menerima ShippingRate");

        Class<?> ch = find("shop.notify.NotificationChannel");
        Class<?> notifier = find("shop.service.Notifier");
        int impls = 0;
        for (String c : List.of("SmsChannel", "WhatsAppChannel", "EmailChannel")) {
            Class<?> k = find("shop.notify." + c);
            if (ch != null && k != null && ch.isAssignableFrom(k)) {
                impls++;
            }
        }
        boolean b = ch != null && ch.isInterface() && impls >= 2 && notifier != null
                && hasConstructorWith(notifier, List.class);
        System.out.println((b ? "[TERDETEKSI] " : "[BELUM]      ")
                + "Tantangan B: NotificationChannel (>= 2 implementasi), Notifier menerima List");
        System.out.println("Tantangan C diperiksa dosen dari LK.");
    }

    // ================= helper =================

    private static boolean hasConstructorWith(Class<?> k, Class<?> param) {
        return Arrays.stream(k.getConstructors())
                .anyMatch(c -> Arrays.asList(c.getParameterTypes()).contains(param));
    }

    private static void check(String label, boolean ok, String hint) {
        total++;
        if (ok) {
            passed++;
            System.out.println("[LULUS] " + label);
        } else {
            System.out.println("[BELUM] " + label);
            if (hint != null) {
                System.out.println("        -> " + hint);
            }
        }
    }

    private static Class<?> find(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException | LinkageError e) {
            return null;
        }
    }

    private static boolean isClass(String name) {
        Class<?> c = find(name);
        return c != null && !c.isInterface();
    }

    private static String source(String relative) {
        Path p = SRC.resolve(relative);
        try {
            return stripComments(Files.readString(p));
        } catch (IOException e) {
            return null;
        }
    }

    private static String sourcesIn(String dir) {
        Path d = SRC.resolve(dir);
        if (!Files.isDirectory(d)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try (Stream<Path> files = Files.list(d)) {
            for (Path p : files.filter(f -> f.toString().endsWith(".java")).toList()) {
                sb.append(stripComments(Files.readString(p)));
            }
        } catch (IOException e) {
            return null;
        }
        return sb.toString();
    }

    private static String stripComments(String code) {
        return code.replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("//[^\\n]*", "");
    }

    private static String rp(long amount) {
        return "Rp" + String.format(Locale.ROOT, "%,d", amount).replace(',', '.');
    }
}

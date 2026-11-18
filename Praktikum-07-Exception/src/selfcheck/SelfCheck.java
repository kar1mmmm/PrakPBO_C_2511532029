package selfcheck;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import shop.StudentInfo;
import shop.model.PhysicalProduct;
import shop.model.Product;
import shop.shipping.RegularShipping;
import shop.shipping.ShippingService;

/**
 * Automatic checker for the P10 lab (Exception Handling).
 * Run it at any time from the project folder: java -cp bin selfcheck.SelfCheck
 * Do not change this file.
 */
public class SelfCheck {

    /** Thrown by a check that does not pass; the message is the reason. */
    static final class Fail extends RuntimeException {
        private static final long serialVersionUID = 1L;

        Fail(String reason) {
            super(reason);
        }
    }

    @FunctionalInterface
    interface Check {
        void run() throws Exception;
    }

    private static final Logger SHOP_LOGGER = Logger.getLogger("shop");
    private static final int REQUIRED_JAVA = 25;

    private static int passed;
    private static int total;
    private static int bonusPassed;
    private static int bonusTotal;
    private static boolean bonusSection;

    public static void main(String[] args) {
        SHOP_LOGGER.setLevel(Level.OFF); // keep CatalogReader warnings out of this report
        System.out.println("=== SELF CHECK: Praktikum Exception Handling (P10) ===");
        System.out.println("NIM " + StudentInfo.NIM + " | " + StudentInfo.NAME
                + " | Java " + Runtime.version().feature());

        section("L0 - Persiapan");
        check("L0.1 Program berjalan di Java " + REQUIRED_JAVA + " atau lebih baru", () -> {
            int feature = Runtime.version().feature();
            if (feature < REQUIRED_JAVA) {
                throw new Fail("Java yang dipakai versi " + feature
                        + "; pilih JDK 25 (Java: Configure Java Runtime)");
            }
        });
        check("L0.2 NIM dan nama di StudentInfo sudah diisi", () -> {
            if (!StudentInfo.NIM.matches("\\d{8,12}") || StudentInfo.NIM.matches("0+")) {
                throw new Fail("NIM belum diisi dengan benar (hanya angka)");
            }
            if (StudentInfo.NAME.isBlank() || StudentInfo.NAME.startsWith("ISI")) {
                throw new Fail("nama belum diisi");
            }
        });

        section("L1 - Membaca crash dan menangani input");
        check("L1.1 ShopApp tidak crash saat nomor produk berupa huruf", () -> {
            String out = runShopApp("abc\n0\n");
            expectContains(out, "Input harus berupa angka.");
        });
        check("L1.2 ShopApp menolak nomor produk di luar katalog", () -> {
            String out = runShopApp("9\n0\n");
            expectContains(out, "Nomor produk tidak ada.");
        });
        check("L1.3 Jumlah bukan angka atau <= 0 ditolak", () -> {
            String out = runShopApp("1\n-2\n1\nx\n0\n");
            expectContains(out, "Jumlah harus lebih dari 0.");
            expectContains(out, "Input harus berupa angka.");
        });

        section("L3 - Custom exception, Inventory, checkout");
        check("L3.1 ShopException adalah checked exception dengan 2 konstruktor", () -> {
            Class<?> shop = cls("shop.exception.ShopException");
            if (!Exception.class.isAssignableFrom(shop)
                    || RuntimeException.class.isAssignableFrom(shop)) {
                throw new Fail("ShopException harus extends Exception (bukan RuntimeException)");
            }
            ctor(shop, String.class);
            ctor(shop, String.class, Throwable.class);
        });
        check("L3.2 Hierarki exception toko + serialVersionUID", () -> {
            expectParent("shop.exception.OutOfStockException", "shop.exception.ShopException");
            expectParent("shop.exception.PaymentFailedException", "shop.exception.ShopException");
            expectParent("shop.exception.InsufficientBalanceException",
                    "shop.exception.PaymentFailedException");
            for (String name : List.of("ShopException", "OutOfStockException",
                    "PaymentFailedException", "InsufficientBalanceException")) {
                expectSerialVersionUid(cls("shop.exception." + name));
            }
        });
        check("L3.3 Exception membawa data (sku, requested, available, shortfall)", () -> {
            Object out = create(ctor(cls("shop.exception.OutOfStockException"),
                    String.class, int.class, int.class), "KOPI-001", 3, 1);
            expectEquals("getSku()", "KOPI-001", call(out, "getSku"));
            expectEquals("getRequested()", 3, call(out, "getRequested"));
            expectEquals("getAvailable()", 1, call(out, "getAvailable"));
            expectContains(((Throwable) out).getMessage(), "KOPI-001");
            Object low = create(ctor(cls("shop.exception.InsufficientBalanceException"),
                    long.class, long.class), 215_000L, 100_000L);
            expectEquals("getShortfall()", 115_000L, call(low, "getShortfall"));
        });
        check("L3.4 Inventory.reserve: stok kurang -> OutOfStockException, stok tetap", () -> {
            Object inv = newInventory("KOPI-001", 2);
            Throwable thrown = expectThrows(inv, "reserve", "shop.exception.OutOfStockException",
                    "KOPI-001", 3);
            expectEquals("getAvailable()", 2, call(thrown, "getAvailable"));
            expectEquals("stok setelah gagal", 2, available(inv, "KOPI-001"));
            expectThrows(inv, "reserve", "java.lang.IllegalArgumentException", "KOPI-001", 0);
            invoke(inv, "reserve", "KOPI-001", 2);
            expectEquals("stok setelah reserve 2", 0, available(inv, "KOPI-001"));
        });
        check("L3.5 Inventory.release mengembalikan stok", () -> {
            Object inv = newInventory("KOPI-001", 2);
            invoke(inv, "reserve", "KOPI-001", 2);
            invoke(inv, "release", "KOPI-001", 2);
            expectEquals("stok setelah release", 2, available(inv, "KOPI-001"));
        });
        check("L3.6 PaymentMethod.pay(long) bertipe void dan throws PaymentFailedException", () -> {
            Method pay = method(cls("shop.payment.PaymentMethod"), "pay", long.class);
            if (pay.getReturnType() != void.class) {
                throw new Fail("pay(long) masih mengembalikan " + pay.getReturnType().getSimpleName());
            }
            expectDeclares(pay, "shop.exception.PaymentFailedException");
        });
        check("L3.7 EWalletPayment: saldo kurang -> InsufficientBalanceException", () -> {
            Object wallet = newWallet(100_000);
            Throwable thrown = expectThrows(wallet, "pay",
                    "shop.exception.InsufficientBalanceException", 150_000L);
            expectEquals("getShortfall()", 50_000L, call(thrown, "getShortfall"));
            expectEquals("saldo setelah gagal", 100_000L, call(wallet, "getBalance"));
            invoke(wallet, "pay", 40_000L);
            expectEquals("saldo setelah bayar 40000", 60_000L, call(wallet, "getBalance"));
        });
        check("L3.8 CreditCardPayment: limit kurang -> InsufficientBalanceException", () -> {
            Object card = create(ctor(cls("shop.payment.CreditCardPayment"),
                    String.class, long.class), "Uji", 100_000L);
            Throwable thrown = expectThrows(card, "pay",
                    "shop.exception.InsufficientBalanceException", 103_000L);
            expectEquals("getShortfall()", 3_000L, call(thrown, "getShortfall"));
            invoke(card, "pay", 60_000L);
            expectEquals("getUsedCredit()", 60_000L, call(card, "getUsedCredit"));
        });
        check("L3.9 CheckoutService(Inventory); checkout(...) mengembalikan long + throws", () -> {
            Class<?> service = cls("shop.service.CheckoutService");
            ctor(service, cls("shop.service.Inventory"));
            Method checkout = checkoutMethod();
            if (checkout.getReturnType() != long.class) {
                throw new Fail("checkout(...) harus mengembalikan long (jumlah yang dibayar)");
            }
            expectDeclares(checkout, "shop.exception.OutOfStockException");
            expectDeclares(checkout, "shop.exception.PaymentFailedException");
        });
        check("L3.10 Checkout berhasil: jumlah dibayar benar, stok berkurang", () -> {
            Object inv = newInventory("KOPI-001", 2);
            Object wallet = newWallet(1_000_000);
            Object paid = checkout(inv, List.of(coffee()), new RegularShipping(), wallet);
            expectEquals("jumlah dibayar (85000 + ongkir 10000)", 95_000L, paid);
            expectEquals("stok KOPI setelah checkout", 1, available(inv, "KOPI-001"));
            expectEquals("saldo setelah checkout", 905_000L, call(wallet, "getBalance"));
        });
        check("L3.11 Checkout gagal: stok dikembalikan, exception asli dilempar ulang", () -> {
            Object inv = newInventory("KOPI-001", 2);
            invoke(inv, "addStock", "RDG-002", 1);
            Object poor = newWallet(100_000);
            Throwable t1 = checkoutThrows(inv, List.of(coffee(), rendang()),
                    new RegularShipping(), poor);
            expectType(t1, "shop.exception.InsufficientBalanceException");
            expectEquals("stok KOPI setelah gagal bayar", 2, available(inv, "KOPI-001"));
            expectEquals("stok RDG setelah gagal bayar", 1, available(inv, "RDG-002"));
            Object rich = newWallet(1_000_000);
            Throwable t2 = checkoutThrows(inv, List.of(rendang(), rendang()),
                    new RegularShipping(), rich);
            expectType(t2, "shop.exception.OutOfStockException");
            expectEquals("stok RDG setelah stok kurang", 1, available(inv, "RDG-002"));
            expectEquals("saldo setelah stok kurang", 1_000_000L, call(rich, "getBalance"));
        });
        check("L3.12 Checkout keranjang kosong -> IllegalStateException", () -> {
            Object inv = newInventory("KOPI-001", 2);
            Throwable thrown = checkoutThrows(inv, List.of(), new RegularShipping(),
                    newWallet(100_000));
            expectType(thrown, "java.lang.IllegalStateException");
        });
        check("L3.13 ShopApp.buy(...): urutan catch dan pesan [OK]/[SALDO]/[BAYAR]/[STOK]", () -> {
            Object inv = newInventory("KOPI-001", 5);
            invoke(inv, "addStock", "RDG-002", 1);
            Object service = create(ctor(cls("shop.service.CheckoutService"),
                    cls("shop.service.Inventory")), inv);
            ShippingService regular = new RegularShipping();
            String saldo = buy(service, List.of(coffee(), rendang()), regular, newWallet(100_000));
            expectStarts(saldo, "[SALDO]");
            expectContains(saldo, "kurang Rp115000");
            Object bank = create(ctor(cls("shop.payment.BankTransferPayment"), String.class), "Bank Uji");
            invoke(bank, "setOnline", false);
            expectStarts(buy(service, List.of(coffee()), regular, bank), "[BAYAR]");
            expectStarts(buy(service, List.of(rendang(), rendang()), regular,
                    newWallet(1_000_000)), "[STOK]");
            String ok = buy(service, List.of(coffee()), regular, newWallet(1_000_000));
            expectStarts(ok, "[OK]");
            expectContains(ok, "Rp95000");
        });

        section("L4 - try-with-resources dan katalog CSV");
        check("L4.1 CatalogLoadException turunan ShopException dan menyimpan cause", () -> {
            expectParent("shop.exception.CatalogLoadException", "shop.exception.ShopException");
            expectSerialVersionUid(cls("shop.exception.CatalogLoadException"));
            IOException cause = new IOException("uji");
            Throwable e = (Throwable) create(ctor(cls("shop.exception.CatalogLoadException"),
                    String.class, Throwable.class), "pesan", cause);
            if (e.getCause() != cause) {
                throw new Fail("cause tidak diteruskan ke super(message, cause)");
            }
        });
        check("L4.2 katalog.csv: 4 produk terbaca, 3 baris rusak dilewati", () -> {
            if (!Files.exists(Path.of("katalog.csv"))) {
                throw new Fail("katalog.csv tidak ada di folder kerja; jalankan dari folder proyek");
            }
            Object reader = create(ctor(cls("shop.io.CatalogReader")));
            Object products = invoke(reader, "load", Path.of("katalog.csv"));
            expectEquals("jumlah produk", 4, ((List<?>) products).size());
            expectEquals("getSkippedCount()", 3, call(reader, "getSkippedCount"));
        });
        check("L4.3 File tidak ada -> CatalogLoadException dengan cause IOException", () -> {
            Object reader = create(ctor(cls("shop.io.CatalogReader")));
            Throwable thrown = expectThrows(reader, "load", "shop.exception.CatalogLoadException",
                    Path.of("tidak-ada-selfcheck.csv"));
            if (!(thrown.getCause() instanceof IOException)) {
                throw new Fail("cause harus IOException asli, ternyata " + thrown.getCause());
            }
            expectContains(thrown.getMessage(), "tidak-ada-selfcheck.csv");
        });
        check("L4.4 CatalogReader membuka file dengan try-with-resources", () -> {
            Path source = Path.of("src", "shop", "io", "CatalogReader.java");
            if (!Files.exists(source)) {
                throw new Fail("file sumber " + source + " tidak ditemukan; jalankan dari folder proyek");
            }
            String code = Files.readString(source).replaceAll("\\s+", " ");
            if (!code.matches(".*try ?\\( ?BufferedReader \\w+ ?= ?Files\\.newBufferedReader.*")) {
                throw new Fail("tidak ditemukan try (BufferedReader ... = Files.newBufferedReader(...))");
            }
        });

        section("L5 - Evaluasi kode FragileShop");
        check("L5.1 FragileShop.payOrder tidak lagi menelan kegagalan bayar", () -> {
            Class<?> fragile = cls("review.FragileShop");
            Method pay = method(fragile, "payOrder", cls("shop.payment.PaymentMethod"), long.class);
            Throwable thrown = invokeStaticCatching(pay, newWallet(10_000), 50_000L);
            if (thrown == null) {
                throw new Fail("pembayaran gagal tetapi payOrder selesai tanpa exception");
            }
            expectType(thrown, "shop.exception.PaymentFailedException");
        });
        check("L5.2 FragileShop.readCatalogHeader menyertakan penyebab asli", () -> {
            Method read = method(cls("review.FragileShop"), "readCatalogHeader", Path.class);
            Throwable thrown = invokeStaticCatching(read, Path.of("tidak-ada-selfcheck.csv"));
            expectType(thrown, "shop.exception.CatalogLoadException");
            if (!(thrown.getCause() instanceof IOException)) {
                throw new Fail("cause masih " + thrown.getCause() + "; sertakan IOException asli");
            }
        });

        int score = Math.round(passed * 50f / total);
        System.out.println();
        System.out.println("=== HASIL: " + passed + " / " + total
                + " pemeriksaan lulus (nilai SelfCheck " + score + " / 50) ===");

        bonusSection = true;
        // section("Tantangan (opsional, nilai tambah)");
        // check("TA InvoiceWriter.write menulis baris produk dan TOTAL", () -> {
        // Object writer = create(ctor(cls("shop.io.InvoiceWriter")));
        // Path file = Files.createTempFile("invoice-selfcheck", ".txt");
        // try {
        // invoke(writer, "write", file, List.of(coffee(), rendang()), 215_000L);
        // List<String> lines = Files.readAllLines(file);
        // expectEquals("isi invoice", List.of("Kopi Arabika Gayo 250g - Rp85000",
        // "Rendang Kemasan 250g - Rp120000", "TOTAL Rp215000"), lines);
        // } finally {
        // Files.deleteIfExists(file);
        // }
        // });
        // check("TB MinimumOrderException untuk Ekspres di bawah Rp20000", () -> {
        // expectParent("shop.exception.MinimumOrderException",
        // "shop.exception.ShopException");
        // method(ShippingService.class, "minimumOrder");
        // expectEquals("Ekspres minimumOrder()", 20_000L, call(new ExpressShipping(),
        // "minimumOrder"));
        // Product sticker = new PhysicalProduct("STK-009", "Stiker Toko", 15_000, 10);
        // Object inv = newInventory("STK-009", 3);
        // Throwable thrown = checkoutThrows(inv, List.of(sticker), new
        // ExpressShipping(),
        // newWallet(1_000_000));
        // expectType(thrown, "shop.exception.MinimumOrderException");
        // expectEquals("stok setelah ditolak", 3, available(inv, "STK-009"));
        // Object service = create(ctor(cls("shop.service.CheckoutService"),
        // cls("shop.service.Inventory")), inv);
        // expectStarts(buy(service, List.of(sticker), new ExpressShipping(),
        // newWallet(1_000_000)), "[MINIMAL]");
        // });
        // check("TC SuppressedDemo menampilkan exception utama dan suppressed", () -> {
        // Method main = method(cls("shop.demo.SuppressedDemo"), "main",
        // String[].class);
        // String out = captureOutput(() -> main.invoke(null, (Object) new String[0]));
        // expectContains(out.toLowerCase(), "suppressed");
        // });
        // System.out.println();
        // System.out.println("=== TANTANGAN: " + bonusPassed + " / " + bonusTotal + "
        // lulus ===");
    }

    // ---------------------------------------------------------------- reporting

    private static void section(String title) {
        System.out.println();
        System.out.println("-- " + title);
    }

    private static void check(String name, Check check) {
        if (bonusSection) {
            bonusTotal++;
        } else {
            total++;
        }
        try {
            check.run();
            if (bonusSection) {
                bonusPassed++;
            } else {
                passed++;
            }
            System.out.println("  [LULUS] " + name);
        } catch (Fail f) {
            System.out.println("  [GAGAL] " + name);
            System.out.println("          -> " + f.getMessage());
        } catch (Throwable t) {
            System.out.println("  [GAGAL] " + name);
            System.out.println("          -> kode Anda melempar " + describe(t));
        }
    }

    private static String describe(Throwable t) {
        Throwable root = t instanceof InvocationTargetException ite && ite.getCause() != null
                ? ite.getCause()
                : t;
        return root.getClass().getName() + (root.getMessage() == null ? "" : ": " + root.getMessage());
    }

    // ---------------------------------------------------------------- reflection
    // helpers

    private static Class<?> cls(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException | LinkageError e) {
            throw new Fail("class/interface belum ada: " + name);
        }
    }

    private static Constructor<?> ctor(Class<?> type, Class<?>... params) {
        try {
            return type.getConstructor(params);
        } catch (NoSuchMethodException e) {
            throw new Fail("method/konstruktor tidak ditemukan: public " + type.getSimpleName()
                    + "(" + names(params) + ")");
        }
    }

    private static Method method(Class<?> type, String name, Class<?>... params) {
        try {
            return type.getMethod(name, params);
        } catch (NoSuchMethodException e) {
            throw new Fail("method/konstruktor tidak ditemukan: public " + type.getSimpleName()
                    + "." + name + "(" + names(params) + ")");
        }
    }

    private static String names(Class<?>... params) {
        return String.join(", ", Arrays.stream(params).map(Class::getSimpleName).toList());
    }

    private static Object create(Constructor<?> constructor, Object... args) throws Exception {
        try {
            return constructor.newInstance(args);
        } catch (InvocationTargetException e) {
            throw new Fail("kode Anda melempar " + describe(e));
        }
    }

    /** Finds a public method by name and argument count, then calls it. */
    private static Object invoke(Object target, String name, Object... args) throws Exception {
        Method m = findByArity(target.getClass(), name, args.length);
        try {
            return m.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw new Fail("kode Anda melempar " + describe(e) + " pada " + name + "(...)");
        }
    }

    private static Object call(Object target, String name) throws Exception {
        return invoke(target, name);
    }

    private static Method findByArity(Class<?> type, String name, int arity) {
        for (Method m : type.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == arity) {
                m.setAccessible(true);
                return m;
            }
        }
        throw new Fail("method/konstruktor tidak ditemukan: public " + type.getSimpleName()
                + "." + name + "(... " + arity + " parameter)");
    }

    /**
     * Calls a method that must throw the given exception type; returns the
     * exception.
     */
    private static Throwable expectThrows(Object target, String name, String type, Object... args)
            throws Exception {
        Method m = findByArity(target.getClass(), name, args.length);
        try {
            m.invoke(target, args);
        } catch (InvocationTargetException e) {
            expectType(e.getCause(), type);
            return e.getCause();
        }
        throw new Fail(name + "(...) seharusnya melempar " + simple(type) + ", tetapi tidak");
    }

    private static Throwable invokeStaticCatching(Method m, Object... args) throws Exception {
        if (!Modifier.isStatic(m.getModifiers())) {
            throw new Fail(m.getName() + "(...) harus static");
        }
        try {
            m.invoke(null, args);
            return null;
        } catch (InvocationTargetException e) {
            return e.getCause();
        }
    }

    // ---------------------------------------------------------------- shop helpers

    private static Product coffee() {
        return new PhysicalProduct("KOPI-001", "Kopi Arabika Gayo 250g", 85_000, 300);
    }

    private static Product rendang() {
        return new PhysicalProduct("RDG-002", "Rendang Kemasan 250g", 120_000, 400);
    }

    private static Object newInventory(String sku, int qty) throws Exception {
        Object inv = create(ctor(cls("shop.service.Inventory")));
        invoke(inv, "addStock", sku, qty);
        return inv;
    }

    private static int available(Object inventory, String sku) throws Exception {
        return (Integer) invoke(inventory, "available", sku);
    }

    private static Object newWallet(long balance) throws Exception {
        return create(ctor(cls("shop.payment.EWalletPayment"), String.class, long.class),
                "Uji", balance);
    }

    private static Method checkoutMethod() {
        return method(cls("shop.service.CheckoutService"), "checkout", List.class,
                ShippingService.class, cls("shop.payment.PaymentMethod"));
    }

    private static Object checkout(Object inventory, List<Product> items, ShippingService shipping,
            Object payment) throws Exception {
        Object service = create(ctor(cls("shop.service.CheckoutService"),
                cls("shop.service.Inventory")), inventory);
        try {
            return checkoutMethod().invoke(service, items, shipping, payment);
        } catch (InvocationTargetException e) {
            throw new Fail("kode Anda melempar " + describe(e) + " pada checkout(...)");
        }
    }

    private static Throwable checkoutThrows(Object inventory, List<Product> items,
            ShippingService shipping, Object payment)
            throws Exception {
        Object service = create(ctor(cls("shop.service.CheckoutService"),
                cls("shop.service.Inventory")), inventory);
        try {
            checkoutMethod().invoke(service, items, shipping, payment);
        } catch (InvocationTargetException e) {
            return e.getCause();
        }
        throw new Fail("checkout(...) seharusnya gagal (melempar exception), tetapi berhasil");
    }

    private static String buy(Object service, List<Product> cart, ShippingService shipping,
            Object payment) throws Exception {
        Method buy = method(cls("shop.app.ShopApp"), "buy", cls("shop.service.CheckoutService"),
                List.class, ShippingService.class, cls("shop.payment.PaymentMethod"));
        if (!Modifier.isStatic(buy.getModifiers()) || buy.getReturnType() != String.class) {
            throw new Fail("buy(...) harus public static String");
        }
        try {
            return (String) buy.invoke(null, service, cart, shipping, payment);
        } catch (InvocationTargetException e) {
            throw new Fail("buy(...) tidak menangkap " + describe(e));
        }
    }

    // ---------------------------------------------------------------- running
    // ShopApp

    @FunctionalInterface
    interface Action {
        void run() throws Exception;
    }

    private static String captureOutput(Action action) throws Exception {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream capture = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            action.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static String runShopApp(String input) throws Exception {
        Method main = method(cls("shop.app.ShopApp"), "main", String[].class);
        InputStream originalIn = System.in;
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            String out;
            try {
                out = captureOutput(() -> main.invoke(null, (Object) new String[0]));
            } catch (InvocationTargetException e) {
                throw new Fail("ShopApp crash: " + describe(e));
            }
            if (out.contains("Toko tidak bisa dibuka")) {
                throw new Fail("katalog.csv tidak terbaca; jalankan SelfCheck dari folder proyek");
            }
            return out;
        } finally {
            System.setIn(originalIn);
        }
    }

    // ---------------------------------------------------------------- assertions

    private static void expectEquals(String what, Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new Fail("hasil tidak sesuai spesifikasi: " + what + " seharusnya " + expected
                    + ", ternyata " + actual);
        }
    }

    private static void expectContains(String text, String part) {
        if (text == null || !text.contains(part)) {
            throw new Fail("hasil tidak sesuai spesifikasi: tidak ditemukan \"" + part + "\"");
        }
    }

    private static void expectStarts(String text, String prefix) {
        if (text == null || !text.startsWith(prefix)) {
            throw new Fail("hasil tidak sesuai spesifikasi: seharusnya diawali " + prefix
                    + ", ternyata \"" + text + "\"");
        }
    }

    private static void expectType(Throwable thrown, String type) {
        if (thrown == null || !cls(type).isInstance(thrown)) {
            throw new Fail("seharusnya " + simple(type) + ", ternyata "
                    + (thrown == null ? "tidak ada exception" : describe(thrown)));
        }
    }

    private static void expectParent(String child, String parent) {
        Class<?> c = cls(child);
        Class<?> p = cls(parent);
        if (c.getSuperclass() != p) {
            throw new Fail(c.getSimpleName() + " harus extends " + p.getSimpleName()
                    + " (sekarang extends " + c.getSuperclass().getSimpleName() + ")");
        }
    }

    private static void expectSerialVersionUid(Class<?> type) {
        try {
            Field f = type.getDeclaredField("serialVersionUID");
            int mod = f.getModifiers();
            if (!Modifier.isStatic(mod) || !Modifier.isFinal(mod) || f.getType() != long.class) {
                throw new Fail(type.getSimpleName() + ": serialVersionUID harus static final long");
            }
        } catch (NoSuchFieldException e) {
            throw new Fail(type.getSimpleName() + " belum punya serialVersionUID");
        }
    }

    private static void expectDeclares(Method m, String exceptionType) {
        Class<?> wanted = cls(exceptionType);
        for (Class<?> declared : m.getExceptionTypes()) {
            if (declared == wanted) {
                return;
            }
        }
        throw new Fail(m.getName() + "(...) belum menulis throws " + wanted.getSimpleName());
    }

    private static String simple(String type) {
        return type.substring(type.lastIndexOf('.') + 1);
    }
}

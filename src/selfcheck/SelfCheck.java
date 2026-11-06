package selfcheck;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Automatic self check for the in-lab exercises (Latihan L1-L5).
 * It uses reflection, so this file compiles even when your classes are not finished yet.
 *
 * Run: java -cp out selfcheck.SelfCheck
 *
 * Do NOT modify this file.
 */
public class SelfCheck {

    private static int passed = 0;
    private static int total = 0;
    private static final List<String> failures = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== SELF CHECK: Praktikum Abstraction ===\n");

        section("L1 - Abstract class & abstract method");
        check("L1.1 Product adalah abstract class", () ->
                isAbstractClass(cls("shop.model.Product")));
        check("L1.2 Product punya abstract method category()", () ->
                Modifier.isAbstract(cls("shop.model.Product").getDeclaredMethod("category").getModifiers()));
        check("L1.3 PhysicalProduct & DigitalProduct adalah subclass konkret Product", () ->
                isConcreteSubclass("shop.model.PhysicalProduct", "shop.model.Product")
                        && isConcreteSubclass("shop.model.DigitalProduct", "shop.model.Product"));
        check("L1.4 Konstruktor menolak harga negatif (IllegalArgumentException)", () ->
                throwsIllegalArgument(() -> physical("X-1", "Salah", -1, 100)));
        check("L1.5 getInfo() PhysicalProduct sesuai format", () ->
                "[Fisik] Kopi Arabika Gayo 250g - Rp85000".equals(
                        call(physical("KOPI-001", "Kopi Arabika Gayo 250g", 85_000, 300), "getInfo")));
        check("L1.6 ShippingService abstract dengan abstract method ratePerKg()", () ->
                isAbstractClass(cls("shop.shipping.ShippingService"))
                        && Modifier.isAbstract(cls("shop.shipping.ShippingService")
                                .getDeclaredMethod("ratePerKg").getModifiers()));
        check("L1.7 calculateCost(): Reguler 700g=10000, 1001g=20000, 0g=0; Ekspres 700g=18000", () -> {
            Object regular = create("shop.shipping.RegularShipping");
            Object express = create("shop.shipping.ExpressShipping");
            return cost(regular, 700) == 10_000 && cost(regular, 1001) == 20_000
                    && cost(regular, 0) == 0 && cost(express, 700) == 18_000;
        });

        section("L2 - Interface & program to an interface");
        check("L2.1 Shippable interface; PhysicalProduct implements, DigitalProduct tidak", () -> {
            Class<?> shippable = cls("shop.shipping.Shippable");
            return shippable.isInterface()
                    && shippable.isAssignableFrom(cls("shop.model.PhysicalProduct"))
                    && !shippable.isAssignableFrom(cls("shop.model.DigitalProduct"));
        });
        check("L2.2 PaymentMethod interface dengan getName() dan pay(long)", () -> {
            Class<?> pm = cls("shop.payment.PaymentMethod");
            return pm.isInterface() && pm.getMethod("getName") != null
                    && pm.getMethod("pay", long.class).getReturnType() == boolean.class;
        });
        check("L2.3 EWalletPayment: bayar sukses mengurangi saldo, saldo kurang ditolak", () -> {
            Object wallet = create("shop.payment.EWalletPayment",
                    new Class<?>[] {String.class, long.class}, "DompetKu", 100_000L);
            boolean ok = (boolean) call(wallet, "pay", new Class<?>[] {long.class}, 60_000L);
            boolean rejected = !(boolean) call(wallet, "pay", new Class<?>[] {long.class}, 50_000L);
            return ok && rejected && (long) call(wallet, "getBalance") == 40_000L;
        });
        check("L2.4 CheckoutService.checkout(...) menerima PaymentMethod & ShippingService (bukan class konkret)", () -> {
            Method m = cls("shop.service.CheckoutService").getMethod("checkout",
                    List.class, cls("shop.shipping.ShippingService"), cls("shop.payment.PaymentMethod"));
            return m.getReturnType() == boolean.class;
        });
        check("L2.5 Total keranjang contoh: Reguler=260000, Ekspres=268000", () -> {
            Object service = create("shop.service.CheckoutService");
            List<Object> cart = sampleCart();
            Class<?> shipping = cls("shop.shipping.ShippingService");
            long regular = (long) call(service, "total", new Class<?>[] {List.class, shipping},
                    cart, create("shop.shipping.RegularShipping"));
            long express = (long) call(service, "total", new Class<?>[] {List.class, shipping},
                    cart, create("shop.shipping.ExpressShipping"));
            return regular == 260_000L && express == 268_000L;
        });

        section("L3 - Default, static, private method; interface inheritance");
        check("L3.1 Discountable: applyDiscount default, percentOf static, ada private method", () -> {
            Class<?> d = cls("shop.discount.Discountable");
            Method apply = d.getMethod("applyDiscount", long.class);
            Method percentOf = d.getMethod("percentOf", long.class, int.class);
            boolean hasPrivate = false;
            for (Method m : d.getDeclaredMethods()) {
                if (Modifier.isPrivate(m.getModifiers())) {
                    hasPrivate = true;
                }
            }
            return d.isInterface() && apply.isDefault()
                    && Modifier.isStatic(percentOf.getModifiers()) && hasPrivate;
        });
        check("L3.2 DigitalProduct implements Discountable; harga akhir 50000 -> 45000", () -> {
            Object ebook = digital("EBK-003", "E-book", 50_000);
            return cls("shop.discount.Discountable").isInstance(ebook)
                    && (long) call(ebook, "getFinalPrice") == 45_000L;
        });
        check("L3.3 RefundablePayment & InstallmentPayment adalah interface turunan PaymentMethod", () -> {
            Class<?> pm = cls("shop.payment.PaymentMethod");
            Class<?> r = cls("shop.payment.RefundablePayment");
            Class<?> i = cls("shop.payment.InstallmentPayment");
            return r.isInterface() && i.isInterface()
                    && List.of(r.getInterfaces()).contains(pm) && List.of(i.getInterfaces()).contains(pm);
        });
        check("L3.4 CreditCardPayment implements RefundablePayment DAN InstallmentPayment", () -> {
            Class<?> card = cls("shop.payment.CreditCardPayment");
            return cls("shop.payment.RefundablePayment").isAssignableFrom(card)
                    && cls("shop.payment.InstallmentPayment").isAssignableFrom(card)
                    && !cls("shop.payment.RefundablePayment").isAssignableFrom(cls("shop.payment.BankTransferPayment"));
        });
        check("L3.5 monthlyInstallment(100000, 3)=33334 dan 13 bulan ditolak", () -> {
            Object card = create("shop.payment.CreditCardPayment",
                    new Class<?>[] {String.class, long.class}, "Uji", 1_000_000L);
            Class<?>[] types = {long.class, int.class};
            long monthly = (long) call(card, "monthlyInstallment", types, 100_000L, 3);
            boolean rejected = throwsIllegalArgument(() -> call(card, "monthlyInstallment", types, 100_000L, 13));
            return monthly == 33_334L && rejected;
        });
        check("L3.6 CheckoutService.refund(...) hanya menerima RefundablePayment", () ->
                cls("shop.service.CheckoutService").getMethod("refund",
                        cls("shop.payment.RefundablePayment"), long.class) != null);

        section("L4 - final method & final class");
        check("L4.1 ShippingService.calculateCost(int) adalah final method", () ->
                Modifier.isFinal(cls("shop.shipping.ShippingService")
                        .getDeclaredMethod("calculateCost", int.class).getModifiers()));
        check("L4.2 Address final class, semua field private final, tanpa setter", () -> {
            Class<?> a = cls("shop.model.Address");
            if (!Modifier.isFinal(a.getModifiers()) || a.getDeclaredFields().length == 0) {
                return false;
            }
            for (Field f : a.getDeclaredFields()) {
                int mod = f.getModifiers();
                if (!Modifier.isPrivate(mod) || !Modifier.isFinal(mod)) {
                    return false;
                }
            }
            for (Method m : a.getDeclaredMethods()) {
                if (m.getName().startsWith("set")) {
                    return false;
                }
            }
            return true;
        });

        section("L5 - Static nested, inner, dan anonymous class");
        check("L5.1 Order.Line static nested; Order.Receipt inner (non-static)", () -> {
            Class<?> line = cls("shop.model.Order$Line");
            Class<?> receipt = cls("shop.model.Order$Receipt");
            return Modifier.isStatic(line.getModifiers()) && !Modifier.isStatic(receipt.getModifiers());
        });
        check("L5.2 Receipt.toText() memuat id pesanan, total barang, dan kota tujuan", () -> {
            Object address = create("shop.model.Address",
                    new Class<?>[] {String.class, String.class, String.class}, "Jl. Uji 1", "Bukittinggi", "26111");
            Object order = create("shop.model.Order",
                    new Class<?>[] {String.class, cls("shop.model.Address")}, "ORD-TEST-9", address);
            call(order, "addLine", new Class<?>[] {cls("shop.model.Product"), int.class},
                    physical("KOPI-001", "Kopi", 85_000, 300), 2);
            Object receipt = call(order, "createReceipt");
            String text = (String) call(receipt, "toText");
            return (long) call(order, "getItemsTotal") == 170_000L
                    && text.contains("ORD-TEST-9") && text.contains("170000") && text.contains("Bukittinggi");
        });
        check("L5.3 ShopApp memakai anonymous class yang mengimplementasikan PaymentMethod", () -> {
            Class<?> pm = cls("shop.payment.PaymentMethod");
            for (int i = 1; i <= 10; i++) {
                try {
                    Class<?> c = Class.forName("shop.app.ShopApp$" + i);
                    if (c.isAnonymousClass() && pm.isAssignableFrom(c)) {
                        return true;
                    }
                } catch (ClassNotFoundException e) {
                    // try the next number
                }
            }
            return false;
        });

        System.out.println("\n=== HASIL: " + passed + " / " + total + " pemeriksaan lulus ===");
        if (!failures.isEmpty()) {
            System.out.println("Belum lulus:");
            for (String f : failures) {
                System.out.println("  - " + f);
            }
        }
    }

    // ---------------------------------------------------------------- helpers

    @FunctionalInterface
    private interface Check {
        boolean run() throws Exception;
    }

    @FunctionalInterface
    private interface Action {
        void run() throws Exception;
    }

    private static void section(String title) {
        System.out.println("\n-- " + title);
    }

    private static void check(String name, Check check) {
        total++;
        String reason;
        try {
            if (check.run()) {
                passed++;
                System.out.println("  [LULUS] " + name);
                return;
            }
            reason = "hasil tidak sesuai spesifikasi";
        } catch (ClassNotFoundException e) {
            reason = "class/interface belum ada: " + e.getMessage();
        } catch (NoSuchMethodException e) {
            reason = "method/konstruktor tidak ditemukan: " + e.getMessage();
        } catch (InvocationTargetException e) {
            reason = "kode Anda melempar " + e.getCause();
        } catch (Exception | LinkageError e) {
            reason = e.getClass().getSimpleName() + ": " + e.getMessage();
        }
        failures.add(name + " (" + reason + ")");
        System.out.println("  [GAGAL] " + name + "\n          -> " + reason);
    }

    private static Class<?> cls(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    private static boolean isAbstractClass(Class<?> c) {
        return !c.isInterface() && Modifier.isAbstract(c.getModifiers());
    }

    private static boolean isConcreteSubclass(String sub, String sup) throws ClassNotFoundException {
        Class<?> c = cls(sub);
        return c.getSuperclass() == cls(sup) && !Modifier.isAbstract(c.getModifiers());
    }

    private static Object create(String className) throws Exception {
        return create(className, new Class<?>[0]);
    }

    private static Object create(String className, Class<?>[] types, Object... args) throws Exception {
        Constructor<?> ctor = cls(className).getConstructor(types);
        return ctor.newInstance(args);
    }

    private static Object call(Object target, String method) throws Exception {
        return call(target, method, new Class<?>[0]);
    }

    private static Object call(Object target, String method, Class<?>[] types, Object... args) throws Exception {
        Method m = target.getClass().getMethod(method, types);
        m.setAccessible(true); // public methods of non-public classes (e.g. anonymous) need this
        return m.invoke(target, args);
    }

    private static Object physical(String sku, String name, long price, int grams) throws Exception {
        return create("shop.model.PhysicalProduct",
                new Class<?>[] {String.class, String.class, long.class, int.class}, sku, name, price, grams);
    }

    private static Object digital(String sku, String name, long price) throws Exception {
        return create("shop.model.DigitalProduct",
                new Class<?>[] {String.class, String.class, long.class, String.class},
                sku, name, price, "https://toko-umkm.example/unduh/" + sku);
    }

    private static long cost(Object shipping, int grams) throws Exception {
        return (long) call(shipping, "calculateCost", new Class<?>[] {int.class}, grams);
    }

    private static List<Object> sampleCart() throws Exception {
        return List.of(
                physical("KOPI-001", "Kopi Arabika Gayo 250g", 85_000, 300),
                physical("RDG-002", "Rendang Kemasan 250g", 120_000, 400),
                digital("EBK-003", "E-book Resep Kue Nusantara", 50_000));
    }

    private static boolean throwsIllegalArgument(Action action) {
        try {
            action.run();
            return false;
        } catch (InvocationTargetException e) {
            return e.getCause() instanceof IllegalArgumentException;
        } catch (IllegalArgumentException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

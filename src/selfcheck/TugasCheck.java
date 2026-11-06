package selfcheck;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Automatic check for the take-home assignment (T1 and T2).
 * It checks that the constants in shop.StudentInfo match the NIM written there.
 *
 * Run: java -cp out selfcheck.TugasCheck
 *
 * Do NOT modify this file.
 */
public class TugasCheck {

    private static int passed = 0;
    private static int total = 0;
    private static final List<String> failures = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== TUGAS CHECK: Praktikum Abstraction ===");

        String nim;
        int expectedPercent;
        long expectedFee;
        int expectedSms;
        try {
            Class<?> info = Class.forName("shop.StudentInfo");
            nim = (String) info.getField("NIM").get(null);
            if (nim == null || !nim.matches("\\d{4,}")) {
                System.out.println("NIM di shop.StudentInfo belum diisi dengan benar.");
                return;
            }
            int lastDigit = nim.charAt(nim.length() - 1) - '0';
            int lastTwo = Integer.parseInt(nim.substring(nim.length() - 2));
            expectedPercent = 1 + (lastDigit % 3);
            expectedFee = 2_500 + 500L * (lastDigit % 5);
            expectedSms = 100 + lastTwo;
            System.out.println("NIM: " + nim + " | " + info.getField("NAME").get(null));
        } catch (ReflectiveOperationException e) {
            System.out.println("shop.StudentInfo belum ada atau tidak lengkap: " + e);
            return;
        }
        final int percent = expectedPercent;
        final long fee = expectedFee;
        final int sms = expectedSms;

        section("Parameter NIM");
        check("P.1 Konstanta di StudentInfo sesuai NIM (" + percent + "%, Rp" + fee + ", " + sms + " karakter)", () -> {
            Class<?> info = cls("shop.StudentInfo");
            return info.getField("CARD_ADMIN_PERCENT").getInt(null) == percent
                    && info.getField("TRANSFER_ADMIN_FEE").getLong(null) == fee
                    && info.getField("SMS_MAX_LENGTH").getInt(null) == sms;
        });

        section("T1 - Evolusi API & diamond");
        check("T1.1 PaymentMethod.adminFee(long) adalah default method; e-wallet = 0", () -> {
            Method m = cls("shop.payment.PaymentMethod").getMethod("adminFee", long.class);
            Object wallet = create("shop.payment.EWalletPayment",
                    new Class<?>[] {String.class, long.class}, "DompetKu", 100_000L);
            return m.isDefault() && (long) call(wallet, "adminFee", new Class<?>[] {long.class}, 100_000L) == 0L;
        });
        check("T1.2 Biaya admin kartu kredit & transfer sesuai parameter NIM", () -> {
            Object card = create("shop.payment.CreditCardPayment",
                    new Class<?>[] {String.class, long.class}, "Uji", 1_000_000L);
            Object transfer = create("shop.payment.BankTransferPayment",
                    new Class<?>[] {String.class}, "Bank Uji");
            Class<?>[] t = {long.class};
            return (long) call(card, "adminFee", t, 200_000L) == 200_000L * percent / 100
                    && (long) call(transfer, "adminFee", t, 200_000L) == fee;
        });
        check("T1.3 grandTotal(...) = total + biaya admin (kartu kredit, keranjang contoh, Ekspres)", () -> {
            Object service = create("shop.service.CheckoutService");
            Object card = create("shop.payment.CreditCardPayment",
                    new Class<?>[] {String.class, long.class}, "Uji", 1_000_000L);
            long grand = (long) call(service, "grandTotal",
                    new Class<?>[] {List.class, cls("shop.shipping.ShippingService"), cls("shop.payment.PaymentMethod")},
                    sampleCart(), create("shop.shipping.ExpressShipping"), card);
            return grand == 268_000L + 268_000L * percent / 100;
        });
        check("T1.4 Diamond policy(): kartu kredit menggabungkan kedua kebijakan", () -> {
            Object card = create("shop.payment.CreditCardPayment",
                    new Class<?>[] {String.class, long.class}, "Uji", 1_000_000L);
            Object wallet = create("shop.payment.EWalletPayment",
                    new Class<?>[] {String.class, long.class}, "DompetKu", 100_000L);
            String cardPolicy = (String) call(card, "policy");
            String walletPolicy = (String) call(wallet, "policy");
            Method refundable = cls("shop.payment.RefundablePayment").getMethod("policy");
            Method installment = cls("shop.payment.InstallmentPayment").getMethod("policy");
            return refundable.isDefault() && installment.isDefault()
                    && cardPolicy.contains(walletPolicy) && cardPolicy.length() > walletPolicy.length();
        });

        section("T2 - Sistem notifikasi");
        check("T2.1 NotificationChannel interface; AbstractChannel abstract dengan send(...) final", () -> {
            Class<?> channel = cls("shop.notification.NotificationChannel");
            Class<?> base = cls("shop.notification.AbstractChannel");
            Method send = base.getMethod("send", cls("shop.notification.CustomerContact"), String.class);
            return channel.isInterface() && Modifier.isAbstract(base.getModifiers())
                    && channel.isAssignableFrom(base) && Modifier.isFinal(send.getModifiers());
        });
        check("T2.2 EmailChannel, SmsChannel, WhatsAppChannel adalah subclass AbstractChannel", () -> {
            Class<?> base = cls("shop.notification.AbstractChannel");
            return cls("shop.notification.EmailChannel").getSuperclass() == base
                    && cls("shop.notification.SmsChannel").getSuperclass() == base
                    && cls("shop.notification.WhatsAppChannel").getSuperclass() == base;
        });
        check("T2.3 SmsChannel memotong pesan panjang tepat " + sms + " karakter berakhiran \"...\"", () -> {
            Object channel = create("shop.notification.SmsChannel", new Class<?>[] {int.class}, sms);
            boolean ok = (boolean) call(channel, "send",
                    new Class<?>[] {cls("shop.notification.CustomerContact"), String.class},
                    contact("Uji", "", "081234567890"), "x".repeat(300));
            String last = (String) call(channel, "getLastDelivered");
            return ok && last.length() == sms && last.endsWith("...");
        });
        check("T2.4 EmailChannel menolak kontak tanpa email yang valid", () -> {
            Object channel = create("shop.notification.EmailChannel");
            Class<?>[] t = {cls("shop.notification.CustomerContact"), String.class};
            boolean rejected = !(boolean) call(channel, "send", t, contact("Uji", "", "081234567890"), "Halo");
            boolean accepted = (boolean) call(channel, "send", t, contact("Uji", "uji@example.com", ""), "Halo");
            return rejected && accepted && (int) call(channel, "getSentCount") == 1;
        });
        check("T2.5 NotificationService.broadcast(...) mengembalikan jumlah kanal yang berhasil", () -> {
            Object service = create("shop.notification.NotificationService");
            Class<?> channelType = cls("shop.notification.NotificationChannel");
            call(service, "register", new Class<?>[] {channelType}, create("shop.notification.EmailChannel"));
            call(service, "register", new Class<?>[] {channelType},
                    create("shop.notification.SmsChannel", new Class<?>[] {int.class}, 120));
            call(service, "register", new Class<?>[] {channelType}, create("shop.notification.WhatsAppChannel"));
            int delivered = (int) call(service, "broadcast",
                    new Class<?>[] {cls("shop.notification.CustomerContact"), String.class},
                    contact("Uji", "", "+6281298765432"), "Pesanan dikirim");
            return delivered == 1; // only WhatsApp accepts +62 without email
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
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            reason = "member tidak ditemukan: " + e.getMessage();
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
        m.setAccessible(true);
        return m.invoke(target, args);
    }

    private static Object contact(String name, String email, String phone) throws Exception {
        return create("shop.notification.CustomerContact",
                new Class<?>[] {String.class, String.class, String.class}, name, email, phone);
    }

    private static List<Object> sampleCart() throws Exception {
        Class<?>[] physical = {String.class, String.class, long.class, int.class};
        return List.of(
                create("shop.model.PhysicalProduct", physical, "KOPI-001", "Kopi Arabika Gayo 250g", 85_000L, 300),
                create("shop.model.PhysicalProduct", physical, "RDG-002", "Rendang Kemasan 250g", 120_000L, 400),
                create("shop.model.DigitalProduct",
                        new Class<?>[] {String.class, String.class, long.class, String.class},
                        "EBK-003", "E-book Resep Kue Nusantara", 50_000L, "https://toko-umkm.example/unduh/ebk-003"));
    }
}

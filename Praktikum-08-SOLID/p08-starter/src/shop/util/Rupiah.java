package shop.util;

import java.util.Locale;

/** Utilitas kecil untuk menampilkan uang (long, rupiah). */
public final class Rupiah {
    private Rupiah() {
    }

    /** 85000 -> "Rp85.000" */
    public static String format(long amount) {
        return "Rp" + String.format(Locale.ROOT, "%,d", amount).replace(',', '.');
    }
}

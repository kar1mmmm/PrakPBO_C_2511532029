package tools;

/** Parameter praktikum yang bergantung pada dua digit terakhir NIM. Jangan diubah. */
public final class NimParams {
    private NimParams() {
    }

    /** Dua digit terakhir NIM (00-99). */
    public static int lastTwo() {
        String nim = StudentInfo.NIM.trim();
        if (!nim.matches("\\d{2,}")) {
            throw new IllegalStateException(
                    "NIM belum diisi dengan benar di tools/StudentInfo.java (hanya angka): " + nim);
        }
        return Integer.parseInt(nim.substring(nim.length() - 2));
    }

    /** Saldo e-wallet: 150.000 + (n mod 4) x 50.000 */
    public static long ewalletBalance() {
        return 150_000L + (lastTwo() % 4) * 50_000L;
    }

    /** Diskon member GOLD (persen): 5 + (n mod 3) x 5 */
    public static int goldPercent() {
        return 5 + (lastTwo() % 3) * 5;
    }

    /** Tarif ongkir per kg: 8.000 + (digit kedua terakhir mod 2) x 2.000 */
    public static long ratePerKg() {
        return 8_000L + ((lastTwo() / 10) % 2) * 2_000L;
    }
}

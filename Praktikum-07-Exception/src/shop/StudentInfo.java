package shop;

/** Your identity. Fill in NIM and NAME before running SelfCheck (L0). */
public final class StudentInfo {
    public static final String NIM = "2511532029"; // TODO L0: your NIM
    public static final String NAME = "ABDUL KARIM ALGAZALI"; // TODO L0: your name

    private StudentInfo() {
    }

    /** Starting e-wallet balance: Rp150,000 to Rp300,000, depends on the NIM. */
    public static long walletBalance() {
        return walletBalance(NIM);
    }

    /** Starting stock of KOPI-001: 2 to 4. */
    public static int coffeeStock() {
        return coffeeStock(NIM);
    }

    /** Starting stock of RDG-002: 1 or 2. */
    public static int rendangStock() {
        return rendangStock(NIM);
    }

    /** Starting stock for any SKU in the shop (5 for other products). */
    public static int stockFor(String sku) {
        return switch (sku) {
            case "KOPI-001" -> coffeeStock();
            case "RDG-002" -> rendangStock();
            default -> 5;
        };
    }

    public static long walletBalance(String nim) {
        return 150_000 + (lastTwoDigits(nim) % 4) * 50_000L;
    }

    public static int coffeeStock(String nim) {
        return 2 + (lastTwoDigits(nim) % 10) % 3;
    }

    public static int rendangStock(String nim) {
        return 1 + (lastTwoDigits(nim) / 10) % 2;
    }

    private static int lastTwoDigits(String nim) {
        String digits = nim.replaceAll("\\D", "");
        if (digits.length() < 2) {
            return 0;
        }
        return Integer.parseInt(digits.substring(digits.length() - 2));
    }
}

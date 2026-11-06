package tools;

/**
 * Prints the personal parameters for the take-home assignment, derived from a student ID (NIM).
 *
 * Run: java -cp out tools.NimParams 2401010057
 */
public class NimParams {

    public static void main(String[] args) {
        if (args.length != 1 || !args[0].matches("\\d{4,}")) {
            System.out.println("Cara pakai: java -cp out tools.NimParams <NIM>");
            System.out.println("NIM harus berupa angka, minimal 4 digit.");
            return;
        }
        String nim = args[0];
        int lastDigit = nim.charAt(nim.length() - 1) - '0';
        int lastTwoDigits = Integer.parseInt(nim.substring(nim.length() - 2));

        System.out.println("Parameter tugas untuk NIM " + nim);
        System.out.println("  CARD_ADMIN_PERCENT  = " + cardAdminPercent(lastDigit));
        System.out.println("  TRANSFER_ADMIN_FEE  = " + transferAdminFee(lastDigit));
        System.out.println("  SMS_MAX_LENGTH      = " + smsMaxLength(lastTwoDigits));
    }

    public static int cardAdminPercent(int lastDigit) {
        return 1 + (lastDigit % 3);
    }

    public static long transferAdminFee(int lastDigit) {
        return 2_500 + 500L * (lastDigit % 5);
    }

    public static int smsMaxLength(int lastTwoDigits) {
        return 100 + lastTwoDigits;
    }
}

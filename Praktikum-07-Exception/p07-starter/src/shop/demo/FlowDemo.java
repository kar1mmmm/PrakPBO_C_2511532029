package shop.demo;

/** L2: write your prediction on the worksheet FIRST, then run this program. */
public class FlowDemo {

    // Part A: try-catch-finally
    static void partA(String input) {
        try {
            System.out.println("A1 mulai");
            int quantity = Integer.parseInt(input);
            System.out.println("A2 jumlah = " + quantity);
        } catch (NumberFormatException e) {
            System.out.println("A3 bukan angka");
        } finally {
            System.out.println("A4 finally");
        }
        System.out.println("A5 lanjut");
    }

    // Part B: a catch that does not match
    static long partB(long total, String monthsText) {
        try {
            int months = Integer.parseInt(monthsText);
            System.out.println("B1 bulan = " + months);
            long monthly = total / months;
            System.out.println("B2 cicilan = " + monthly);
            return monthly;
        } catch (NumberFormatException e) {
            System.out.println("B3 bukan angka");
            return 0;
        } finally {
            System.out.println("B4 finally");
        }
    }

    // Part C: return inside try
    static int partC(int stock) {
        try {
            System.out.println("C1 cek stok");
            return stock - 1;
        } finally {
            System.out.println("C2 finally");
        }
    }

    public static void main(String[] args) {
        System.out.println("== Bagian A ==");
        partA("12");
        partA("12rb");

        System.out.println("== Bagian B ==");
        try {
            System.out.println("B5 hasil = " + partB(300_000, "3"));
            System.out.println("B5 hasil = " + partB(300_000, "0"));
            System.out.println("B6 baris ini?");
        } catch (ArithmeticException e) {
            System.out.println("B7 main menangkap: " + e.getMessage());
        }

        System.out.println("== Bagian C ==");
        System.out.println("C3 sisa = " + partC(5));
    }
}

package tools;

import shop.StudentInfo;

/** Prints the lab parameters for a NIM. Run: java -cp bin tools.NimParams [NIM] */
public class NimParams {

    public static void main(String[] args) {
        String nim = args.length > 0 ? args[0] : StudentInfo.NIM;
        System.out.println("Parameter praktikum P10 untuk NIM " + nim);
        System.out.println("  Saldo awal e-wallet DompetKu : Rp" + StudentInfo.walletBalance(nim));
        System.out.println("  Stok awal KOPI-001           : " + StudentInfo.coffeeStock(nim));
        System.out.println("  Stok awal RDG-002            : " + StudentInfo.rendangStock(nim));
        System.out.println("  Stok awal produk lain        : 5");
    }
}

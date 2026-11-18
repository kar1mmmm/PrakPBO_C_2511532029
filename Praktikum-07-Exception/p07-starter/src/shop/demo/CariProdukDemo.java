package shop.demo;

/** L2 part D: run from the terminal with different arguments. */
public class CariProdukDemo {

    public static void main(String[] args) {
        String[] skus = {"KOPI-001", "RDG-002", "TEH-004"};
        try {
            if (args.length == 0) {
                throw new IllegalArgumentException("Berikan satu nomor produk.");
            }
            int number = Integer.parseInt(args[0]);
            System.out.println("D1 SKU: " + skus[number - 1]);
        } catch (NumberFormatException e) {
            System.out.println("D2 nomor harus bilangan bulat");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("D3 nomor di luar katalog: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("D4 " + e.getMessage());
        } finally {
            System.out.println("D5 pencarian selesai");
        }
    }
}

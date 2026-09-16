package pekan2;
public class Transaksi {
    String idTransaksi;
    String jenis;
    double nominal;

    public Transaksi(String id, String jenis, double nominal) {
        this.idTransaksi = id;
        this.jenis = jenis;
        this.nominal = nominal;
    }

    public void cetakTransaksi() {
        System.out.println("ID Transaksi: " + idTransaksi + ", Jenis: " + jenis + ", Nominal: Rp " + nominal);
    }

    public void cetakDetail() {
        cetakTransaksi();
    }
}
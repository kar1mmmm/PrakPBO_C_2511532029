package pekan4	;
public class Transaksi {
    String idTransaksi;
    String jenis;
    double nominal;

    public Transaksi(String id, String jenis, double nominal) {
        this.idTransaksi = id;
        this.jenis = jenis;
        this.nominal = nominal;
    }
    
    public String getIdTransaksi() {return idTransaksi;}
    public String getJenis() {return jenis;}
    public double getNominal() {return nominal;}
    

    public void cetakDetail() {
        System.out.println("ID Transaksi: " + idTransaksi + ", Jenis: " + jenis + ", Nominal: Rp " + nominal);
    }

   
}
package pekan4;

public class RekeningTabungan extends Rekening {
	private double sukuBunga;

	public RekeningTabungan(String nomor, String nama, double saldoAwal, String pinAwal, double sukuBunga) {
		super(nomor, nama, saldoAwal, pinAwal);
		this.sukuBunga = sukuBunga;
	}

	public double getSukuBunga() {return sukuBunga;}

	public void tambahBungaAkhirBulan() {
		double bunga = getSaldo() * sukuBunga / 100;
		kredit(bunga, "Bunga Akhir Bulan");
		System.out.println("Bunga akhir bulan sebesar Rp " + bunga + " (" + sukuBunga + "%) berhasil ditambahkan.");
		System.out.println("Saldo terkini: Rp " + getSaldo());
	}
}

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

	public void tambahBungaAkhirTahun() {
		if (getSaldo() <= 10_000_000) {
			System.out.println("Gagal: Saldo saat ini (Rp " + getSaldo() + ") tidak melebihi Rp 10.000.000. Bunga akhir tahun tidak diberikan.");
			return;
		}
		double bunga = getSaldo() * sukuBunga / 100;
		kredit(bunga, "Bunga Akhir Tahun");
		System.out.println("Bunga akhir tahun sebesar Rp " + bunga + " (" + sukuBunga + "%) berhasil ditambahkan.");
		System.out.println("Saldo terkini: Rp " + getSaldo());
	}
}

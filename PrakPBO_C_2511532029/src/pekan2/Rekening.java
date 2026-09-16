package pekan2;

import java.util.ArrayList;

public class Rekening {
	String nomorRekening;
	String namaPemilik;
	double saldo;

	ArrayList<Transaksi> riwayatTransaksi;

	
	public Rekening(String nomor,String nama, double saldoAwal) {
		nomorRekening = nomor;
		namaPemilik = nama;
		saldo = saldoAwal;

		this.riwayatTransaksi = new ArrayList<>();
		System.out.println("Rekening atas nama" + namaPemilik + " berhasil dibuat dengan saldo Rp" + saldo);
	}
	
	public String getNomorRekening() {
		return nomorRekening;
	}
	
	public void setorTunai(double nominal) {
		if ( nominal > 0) {
			saldo += nominal;
			String idTrx = "TRX-S" + System.currentTimeMillis();
			Transaksi trxBaru = new Transaksi(idTrx, "Kredit", nominal);
			riwayatTransaksi.add(trxBaru);	
			System.out.println("Saldo tunai Rp" + nominal + " berhasil, Saldo saat ini: Rp" + saldo);
		} else {
			System.out.println("Gagal : nominal setor harus lebih dari 0");
		}
	}
	
	public void tarikTunai(double nominal) {
		if (nominal < 10000) {
			System.out.println("Gagal : nominal tarik harus minimal 10000");
		} else if (saldo - nominal < 10000) {
			System.out.println("Gagal: Saldo tidak mencukupi (saldo minimal tersisa Rp10.000).");
		} else {
			saldo -= nominal;
			String idTrx = "TRX-T" + System.currentTimeMillis();
			Transaksi trxBaru = new Transaksi(idTrx, "Debit", nominal);
			riwayatTransaksi.add(trxBaru);
			System.out.println("Tarik tunai Rp" + nominal + " berhasil, Saldo saat ini: Rp" + saldo);
		}
	}
	public void cekInformasi() {
		System.out.println("--- INFO REKENING ---");
		System.out.println("No. Rekening :" + nomorRekening);
		System.out.println("Nama Pemilik :" + namaPemilik);
		System.out.println("Saldo Terakhir: Rp" + saldo);
		System.out.println("-----------------");
	}

	public void cetakMutasi() {
		if (riwayatTransaksi.isEmpty()) {
			System.out.println("Belum ada transaksi pada rekening ini");
		} else {
			for (Transaksi transaksi : riwayatTransaksi) {
				transaksi.cetakDetail();
			}
		}
	}
}






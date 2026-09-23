package pekan3;

import java.util.ArrayList;

public class Rekening {
	private String nomorRekening;
	private String namaPemilik;
	private double saldo;
	private String pin;

	ArrayList<Transaksi> riwayatTransaksi;
	
	public Rekening(String nomor,String nama,double saldoAwal, String pinAwal) {
		this.nomorRekening = nomor;
		this.namaPemilik = nama;
		this.saldo = saldoAwal;
		
		if (pinAwal != null && pinAwal.matches("\\d{6}")) {
			this.pin = pinAwal;
		} else {
			System.out.println("Peringatan PIN harus 6 digit angka! Menggunakan PIN default 123456");
			this.pin = "123456";
		}
		this.riwayatTransaksi = new ArrayList<>();
		System.out.println("Rekening atas nama " + namaPemilik + " Berhasil dibuat");
	}
	
	public String getNomorRekening() {return nomorRekening;}
	public String getNamaPemilik() {return namaPemilik;}
	public double getSaldo() {return saldo;}
	
	public boolean otentikasi(String inputPin) {
		if (inputPin == null || !inputPin.matches("\\d{6}")) {
			return false;
		}
		return this.pin.equals(inputPin);
	}
	
	public boolean tarik(double nominal) {
		if (nominal > saldo) {
			System.out.println("Saldo tidak cukup");
			return false;
		}
		saldo -= nominal;
		String idTx = "TRX" + (riwayatTransaksi.size() + 1);
		riwayatTransaksi.add(new Transaksi(idTx, "Tarik tunai", nominal));
		return false;
	}
	
	public void setor(double nominal) {
		saldo += nominal;
		String idTx = "TRX" + (riwayatTransaksi.size() + 1);
		riwayatTransaksi.add(new Transaksi(idTx, "Setor Tunai", nominal));
	}
	
	
}







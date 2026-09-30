package pekan4;

import java.util.ArrayList;

public class Rekening {
	private String nomorRekening;
	private String namaPemilik;
	private String pin;

	protected double saldo;
	protected ArrayList<Transaksi> riwayatTransaksi;
	
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
		debit(nominal, "Tarik Tunai");
		return true;
	}
	
	public void setor(double nominal) {
		kredit(nominal, "Setor Tunai");
	}
	
	protected void debit(double nominal, String jenis) {
		saldo -= nominal;
		String idTx = "TRX" + (riwayatTransaksi.size() + 1);
		riwayatTransaksi.add(new Transaksi(idTx, jenis, nominal));
	}
	
	protected void kredit(double nominal, String jenis) {
		saldo += nominal;
		String idTx = "TRX" + (riwayatTransaksi.size() + 1);
		riwayatTransaksi.add(new Transaksi(idTx, jenis, nominal));
	}
	
	
}







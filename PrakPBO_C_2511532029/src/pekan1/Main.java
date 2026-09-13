package pekan1;

import java.util.Scanner;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		
		ArrayList<Rekening> daftarRekening = new ArrayList<>();
		Rekening akunAktif = null;
		boolean isRunning = true;
		
		System.out.println("=== SISTEM PERBANKAN MINI ===");
		
		while (isRunning) {
			System.out.println("\nMenu Utama:");
			System.out.println("1. Buka Rekening Baru");
			System.out.println("2. Setor tunai");
			System.out.println("3. Tarik tunai");
			System.out.println("4. Cek Informasi Rekening");
			System.out.println("5. Ganti Akun");
			System.out.println("0. Keluar");
			System.out.print("Pilih Menu : ");
			
			int pilihan = input.nextInt();
			input.nextLine();
			
			switch (pilihan) {
				case 1:
					System.out.print("Masukan nomor rekening: ");
					String no = input.nextLine();
					
					boolean sudahTerdaftar = false;
					for (Rekening r : daftarRekening) {
						if (r.getNomorRekening().equals(no)) { 
							sudahTerdaftar = true;
							break;
						}
					}
					
					if (sudahTerdaftar) {
						System.out.println("Error: Nomor rekening sudah terdaftar!");
					} else {
						System.out.print("Masukan nama pemilik: ");
						String nama = input.nextLine();
						System.out.print("Masukan saldo awal: ");
						double saldo = input.nextDouble();
						
						Rekening rekeningBaru = new Rekening(no, nama, saldo);
						daftarRekening.add(rekeningBaru);
						akunAktif = rekeningBaru; 
						System.out.println("Rekening berhasil dibuat dan langsung aktif!");
					}
					break;
					
				case 2:
					if (akunAktif == null) {
						System.out.println("Error: Mohon maaf, anda belum memiliki nomor rekening / belum login!");
					} else {
						System.out.print("Masukan nominal setor: ");
						double setor = input.nextDouble();
						akunAktif.setorTunai(setor);
					}
					break;
					
				case 3:
					if (akunAktif == null) {
						System.out.println("Error: Mohon maaf, anda belum memiliki nomor rekening / belum login!");
					} else {
						System.out.print("Masukan nominal penarikan: ");
						double tarik = input.nextDouble();
						akunAktif.tarikTunai(tarik);
					}
					break;
					
				case 4:
					if (akunAktif == null) {
						System.out.println("Error : anda belum membuka rekening / belum login");
					} else {
						akunAktif.cekInformasi();
					}
					break;
					
				case 5:
					if (daftarRekening.isEmpty()) {
						System.out.println("Belum ada rekening yang terdaftar. Silakan buka rekening baru terlebih dahulu.");
					} else {
						System.out.print("Masukan nomor rekening yang ingin diaktifkan: ");
						String noTujuan = input.nextLine();
						boolean ditemukan = false;
						
						for (Rekening r : daftarRekening) {
							if (r.getNomorRekening().equals(noTujuan)) {
								akunAktif = r;
								ditemukan = true;
								System.out.println("Berhasil ganti akun!");
								break;
							}
						}
						
						if (!ditemukan) {
							System.out.println("Nomor rekening tidak ditemukan!");
						}
					}
					break;
					
				case 0:
					isRunning = false;
					System.out.println("Sistem ditutup. Terima kasih!");
					break;
					
				default:
					System.out.println("Pilihan tidak valid");
			}
		}

		input.close();
	}
}
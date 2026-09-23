package pekan3;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Rekening akunAktif = null;

        while (true) {
            System.out.println("\n=== SISTEM PERBANKAN ===");
            System.out.println("1. Buka Rekening");
            System.out.println("2. Setor Tunai");
            System.out.println("3. Tarik Tunai");
            System.out.println("4. Transfer");
            System.out.println("5. Cek Saldo");
            System.out.println("6. Cetak Mutasi");
            System.out.println("7. Keluar");
            System.out.print("Pilih menu: ");

            int pilihan = scanner.nextInt();
            scanner.nextLine(); // Membersihkan newline buffer

            switch (pilihan) {
                case 1:
                    // Menu 1: Buka Rekening
                    System.out.print("Masukkan Nomor Rekening : ");
                    String nomor = scanner.nextLine();
                    
                    System.out.print("Masukkan Nama Pemilik   : ");
                    String nama = scanner.nextLine();
                    
                    System.out.print("Masukkan Saldo Awal     : ");
                    double saldoAwal = scanner.nextDouble();
                    scanner.nextLine(); // Clear buffer
                    
                    String pinAwal;
                    while (true) {
                        System.out.print("Buat PIN (6 digit angka): ");
                        pinAwal = scanner.nextLine();
                        if (pinAwal.matches("\\d{6}")) {
                            break;
                        }
                        System.out.println("PIN hanya boleh berisi 6 digit angka!");
                    }

                    akunAktif = new Rekening(nomor, nama, saldoAwal, pinAwal);
                    break;

                case 2: // Setor Tunai
                    if (akunAktif == null) {
                        System.out.println("Belum ada rekening yang dibuat!");
                        break;
                    }
                    System.out.print("Masukkan nominal setor: ");
                    double nominalSetor = scanner.nextDouble();
                    
                    // Panggil method setor agar tercatat di riwayat
                    akunAktif.setor(nominalSetor);
                    System.out.println("Setor tunai sebesar Rp " + nominalSetor + " berhasil.");
                    break;

                case 3: // Tarik Tunai dengan PIN
                    if (akunAktif == null) {
                        System.out.println("Belum ada rekening yang dibuat!");
                        break;
                    }

                    String pinTarik;
                    while (true) {
                        System.out.print("Masukkan PIN Anda: ");
                        pinTarik = scanner.nextLine();
                        if (pinTarik.matches("\\d{6}")) {
                            break;
                        }
                        System.out.println("PIN hanya boleh berisi 6 digit angka!");
                    }

                    if (akunAktif.otentikasi(pinTarik)) {
                        System.out.print("Masukkan nominal tarik: ");
                        double nominalTarik = scanner.nextDouble();
                        
                        // Panggil method tarik agar tercatat di riwayat
                        if (akunAktif.tarik(nominalTarik)) {
                            System.out.println("Tarik tunai sebesar Rp " + nominalTarik + " berhasil.");
                        }
                    } else {
                        System.out.println("Akses Ditolak: PIN yang Anda masukkan salah!");
                    }
                    break;

                case 4:
                    // Transfer
                    if (akunAktif == null) {
                        System.out.println("Belum ada rekening yang dibuat!");
                        break;
                    }
                    System.out.println("Fitur transfer...");
                    break;

                case 5:
                    // Cek Saldo
                    if (akunAktif == null) {
                        System.out.println("Belum ada rekening yang dibuat!");
                        break;
                    }
                    System.out.println("Nama Pemilik : " + akunAktif.getNamaPemilik());
                    System.out.println("No Rekening  : " + akunAktif.getNomorRekening());
                    break;

                case 6:
                    // Menu 6: Cetak Mutasi dengan Otentikasi PIN
                    if (akunAktif == null) {
                        System.out.println("Belum ada rekening yang dibuat!");
                        break;
                    }

                    String pinMutasi;
                    while (true) {
                        System.out.print("Masukkan PIN Anda: ");
                        pinMutasi = scanner.nextLine();
                        if (pinMutasi.matches("\\d{6}")) {
                            break;
                        }
                        System.out.println("PIN hanya boleh berisi 6 digit angka!");
                    }

                    // Verifikasi PIN
                    if (akunAktif.otentikasi(pinMutasi)) {
                        System.out.println("\n--- RIWAYAT TRANSAKSI ---");
                        if (akunAktif.riwayatTransaksi.isEmpty()) {
                            System.out.println("Belum ada transaksi.");
                        } else {
                            for (Transaksi t : akunAktif.riwayatTransaksi) {
                                t.cetakDetail();
                            }
                        }
                    } else {
                        System.out.println("Akses Ditolak: PIN yang Anda masukkan salah!");
                    }
                    break;

                case 7:
                    System.out.println("Terima kasih telah menggunakan layanan kami.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Pilihan tidak valid!");
            }
        }
    }
}
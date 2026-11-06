# Praktikum OOP – Abstraction (Toko Online UMKM)

Zip ini **bukan proyek yang dibuka langsung**. Anda membuat proyek Java baru
di VS Code (modul, Latihan L0), lalu menyalin alat bantu berikut ke dalamnya:

| Salin dari zip | Ke proyek Anda | Kegunaan | Boleh diubah? |
|---|---|---|---|
| `src/selfcheck/` | `src/selfcheck/` | `SelfCheck` (Latihan) dan `TugasCheck` (Tugas) | Tidak |
| `src/tools/` | `src/tools/` | `NimParams`: parameter tugas dari NIM | Tidak |
| `src/review/` | `src/review/` | `BadShop`: bahan code review Tugas T3 | Jangan ubah file aslinya |
| `src/shop/StudentInfo.java` | `src/shop/StudentInfo.java` | Identitas dan parameter tugas | Ya (wajib diisi untuk Tugas) |

## Ringkasan langkah membuat proyek (detail di modul, L0)

1. VS Code: `Ctrl+Shift+P` → **Java: Create Java Project...** → **No build tools**.
2. Pilih folder induk, beri nama proyek `abstraction-<NIM>`.
3. Jalankan `src/App.java` untuk memastikan JDK 21 bekerja, lalu hapus `App.java`.
4. Di `src`, buat folder `shop/model`, `shop/shipping`, `shop/discount`,
   `shop/payment`, `shop/service`, `shop/app`.
5. Salin alat bantu sesuai tabel di atas.
6. Jalankan `src/selfcheck/SelfCheck.java`. Hasil awal yang benar: `0 / 23`.

## Menjalankan program dari terminal (opsional)

Dari folder proyek Anda:

**Windows (CMD):**

```
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out selfcheck.SelfCheck
```

> Di PowerShell, jalankan baris pertama sebagai:
> `cmd /c "dir /s /b src\*.java > sources.txt"`

**Linux/macOS:**

```
javac -d out $(find src -name "*.java")
java -cp out selfcheck.SelfCheck
```

Program lain:

```
java -cp out shop.app.ShopApp
java -cp out selfcheck.TugasCheck
java -cp out tools.NimParams 2401010057
java -cp out review.BadShop
```

## Pengumpulan

- **Latihan (di akhir sesi):** zip folder `src` →
  `LATIHAN_<NIM>_<Nama>.zip`, ditambah tangkapan layar hasil `SelfCheck`.
- **Tugas (di rumah):** zip seluruh folder proyek →
  `TUGAS_<NIM>_<Nama>.zip`, ditambah laporan PDF.

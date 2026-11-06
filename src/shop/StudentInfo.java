package shop;

public class StudentInfo {
    public static final String NIM = "2511532029";
    public static final String NAME = "Mahasiswa";
    
    // Parameter untuk Tugas (dihitung berdasarkan NIM 2511532029: d=9, dd=29)
    public static final int CARD_ADMIN_PERCENT = 1 + (9 % 3); // 1
    public static final long TRANSFER_ADMIN_FEE = 2500 + 500 * (9 % 5); // 4500
    public static final int SMS_MAX_LENGTH = 100 + 29; // 129
}
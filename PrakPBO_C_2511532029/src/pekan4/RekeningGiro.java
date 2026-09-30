package pekan4;

public class RekeningGiro extends Rekening {
	private double batasOverdraft;

	public RekeningGiro(String nomor, String nama, double saldoAwal, String pinAwal, double batasOverdraft) {
		super(nomor, nama, saldoAwal, pinAwal);
		this.batasOverdraft = batasOverdraft;
	}

	public double getBatasOverdraft() {return batasOverdraft;}

	@Override
	public boolean tarik(double nominal) {
		if (nominal > getSaldo() + batasOverdraft) {
			System.out.println("Saldo dan limit overdraft tidak mencukupi!");
			return false;
		}
		debit(nominal, "Tarik Tunai (Giro)");
		return true;
	}
}
	
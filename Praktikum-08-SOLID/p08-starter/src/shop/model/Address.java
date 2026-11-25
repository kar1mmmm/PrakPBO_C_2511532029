package shop.model;

/** Alamat kirim (immutable, dari P9). */
public final class Address {
    private final String recipient;
    private final String city;

    public Address(String recipient, String city) {
        if (recipient == null || recipient.isBlank() || city == null || city.isBlank()) {
            throw new IllegalArgumentException("Penerima dan kota wajib diisi");
        }
        this.recipient = recipient;
        this.city = city;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getCity() {
        return city;
    }
}

package shop.service;

/** Rincian tagihan satu pesanan. */
public record Bill(long subtotal, long discount, long shipping, long total) {
}
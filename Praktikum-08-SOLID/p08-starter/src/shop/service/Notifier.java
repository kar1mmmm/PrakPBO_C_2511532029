package shop.service;

import shop.model.Order;
import shop.util.Rupiah;

public class Notifier {
    public void notifyPaid(Order order, long amount) {
        System.out.println("[SMS] " + order.getAddress().getRecipient() + ": pesanan "
                + order.getOrderId() + " sudah dibayar (" + Rupiah.format(amount) + ")");
    }
}
package shop.service;

import shop.model.Order;
import shop.util.Rupiah;

public class InvoicePrinter {
    public void print(Order order, Bill bill, String paymentName) {
        System.out.println("--- INVOICE " + order.getOrderId() + " ---");
        System.out.println("Kirim ke : " + order.getAddress().getRecipient()
                + ", " + order.getAddress().getCity());
        for (Order.Line line : order.lines()) {
            System.out.println(line.product().getName() + " x" + line.qty()
                    + " = " + Rupiah.format(line.total()));
        }
        System.out.println("Subtotal : " + Rupiah.format(bill.subtotal()));
        System.out.println("Diskon   : " + Rupiah.format(bill.discount()));
        System.out.println("Ongkir   : " + Rupiah.format(bill.shipping()));
        System.out.println("Total    : " + Rupiah.format(bill.total()));
        System.out.println("Bayar    : " + paymentName);
    }
}
package shop.service;

import shop.discount.DiscountStrategy;
import shop.exception.PaymentFailedException;
import shop.model.Order;
import shop.payment.PaymentMethod;

public class CheckoutService {
    private final OrderCalculator calculator;
    private final InvoicePrinter printer;
    private final Notifier notifier;

    public CheckoutService(OrderCalculator calculator, InvoicePrinter printer, Notifier notifier) {
        this.calculator = calculator;
        this.printer = printer;
        this.notifier = notifier;
    }

    public long checkout(Order order, DiscountStrategy discount, PaymentMethod payment)
            throws PaymentFailedException {
        Bill bill = calculator.calculate(order, discount);
        payment.pay(bill.total());
        printer.print(order, bill, payment.name());
        notifier.notifyPaid(order, bill.total());
        return bill.total();
    }
}
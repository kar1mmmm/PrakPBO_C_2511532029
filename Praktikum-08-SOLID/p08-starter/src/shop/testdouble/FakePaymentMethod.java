package shop.testdouble;

import shop.payment.PaymentMethod;

public class FakePaymentMethod implements PaymentMethod {
    private long lastAmount;
    private int callCount;

    public FakePaymentMethod() {
    }

    @Override
    public String name() {
        return "Fake";
    }

    @Override
    public void pay(long amount) {
        this.lastAmount = amount;
        this.callCount++;
    }

    public long getLastAmount() {
        return lastAmount;
    }

    public int getCallCount() {
        return callCount;
    }
}
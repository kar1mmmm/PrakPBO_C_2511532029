package review;

import java.util.ArrayList;
import java.util.List;

/*
 * T3 - CODE REVIEW
 * This file compiles and runs, but its design has several problems.
 * Find them, explain them, and refactor (see the module for instructions).
 */

interface StoreOperations {
    boolean pay(long amount);

    boolean refund(long amount);

    long ship(int weightGrams);

    String printInvoice();
}

class GiftVoucher implements StoreOperations {
    private double balance = 100000.0;

    @Override
    public boolean pay(long amount) {
        if (amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }

    @Override
    public boolean refund(long amount) {
        throw new UnsupportedOperationException("Voucher cannot be refunded");
    }

    @Override
    public long ship(int weightGrams) {
        throw new UnsupportedOperationException("Voucher is not shipped");
    }

    @Override
    public String printInvoice() {
        return "Voucher, remaining balance " + balance;
    }
}

abstract class Courier {
    public long cost(String type, int kilograms) {
        switch (type) {
            case "REGULAR":
                return kilograms * 10_000L;
            case "EXPRESS":
                return kilograms * 18_000L;
            case "SAME_DAY":
                return kilograms * 30_000L;
            default:
                return 0;
        }
    }
}

class JneCourier extends Courier {
}

class Cart {
    private final List<Item> items = new ArrayList<>();

    class Item {
        private final String name;
        private final long price;
        private final int quantity;

        Item(String name, long price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        long subtotal() {
            return price * quantity;
        }

        String getName() {
            return name;
        }
    }

    void add(Item item) {
        items.add(item);
    }

    long total() {
        long total = 0;
        for (Item item : items) {
            total += item.subtotal();
        }
        return total;
    }
}

class Address {
    private String street;
    private String city;

    Address(String street, String city) {
        this.street = street;
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return street + ", " + city;
    }
}

class SimpleOrder {
    private final String id;
    private final Address address;

    SimpleOrder(String id, Address address) {
        this.id = id;
        this.address = address;
    }

    Address getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return id + " -> " + address;
    }
}

interface Discount {
    default long apply(long price) {
        if (this instanceof MemberDiscount) {
            return price - price * 5 / 100;
        } else if (this instanceof FlashSaleDiscount) {
            return price - price * 30 / 100;
        }
        return price;
    }
}

class MemberDiscount implements Discount {
}

class FlashSaleDiscount implements Discount {
}

public class BadShop {
    public static void main(String[] args) {
        Cart cart = new Cart();
        cart.add(cart.new Item("Kopi Gayo", 85_000, 2));
        System.out.println("Total cart: " + cart.total());

        Courier courier = new JneCourier();
        System.out.println("Ongkir EXPRES 2 kg: " + courier.cost("EXPRES", 2));

        Address home = new Address("Jl. Sudirman 10", "Padang");
        SimpleOrder first = new SimpleOrder("ORD-1", home);
        SimpleOrder second = new SimpleOrder("ORD-2", home);
        second.getAddress().setCity("Bukittinggi");
        System.out.println(first);
        System.out.println(second);

        Discount flashSale = new FlashSaleDiscount();
        System.out.println("Harga flash sale: " + flashSale.apply(100_000));

        StoreOperations voucher = new GiftVoucher();
        System.out.println(voucher.pay(30_000) + " | " + voucher.printInvoice());
        System.out.println("Ongkir voucher: " + voucher.ship(100));
    }
}

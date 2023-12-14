package main.java.gookjiii.taxi;

import java.util.concurrent.ThreadLocalRandom;

public class Order {

    private final String name;

    private OrderType orderType;

    private static final int lowerBound = 100;
    private static final int upperBound = 105;
    private final int suspendedTime;

    public Order(String name, OrderType orderType) {
        this.name = name;
        this.orderType = orderType;
        this.suspendedTime = ThreadLocalRandom.current().nextInt(lowerBound, upperBound + 1);
    }

    public void execute () throws InterruptedException {
        Thread.sleep(suspendedTime);
    }

    public String getName() {
        return this.name;
    }

    public OrderType getOrderType() {
        return this.orderType;
    }

    public String toString() {
        return this.name;
    }

}

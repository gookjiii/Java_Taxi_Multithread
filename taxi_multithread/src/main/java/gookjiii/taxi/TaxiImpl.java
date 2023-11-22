package main.java.gookjiii.taxi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaxiImpl extends Thread implements Taxi {

    private final String name;
    private final Dispatcher dispatcher;
    private final List<Order> executedOrders;
    private final OrderWrapper currentOrder;
    private final AtomicBoolean execute;

    public TaxiImpl(String name, Dispatcher dispatcher, AtomicBoolean execute) {
        super(name);
        this.name = name;
        this.dispatcher = dispatcher;
        this.execute = execute;
        this.executedOrders = new ArrayList<>();
        this.currentOrder = new OrderWrapper();
    }

    @Override
    public void run() {
        while (execute.get()) {
            synchronized (currentOrder) {
                while (currentOrder.isNull()) {
                    try {
                        currentOrder.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException("Error during execution", e);
                    }
                }
                if (currentOrder.getOrder().getOrderType() != OrderType.COMPLETE) {
                    try {
                        synchronized (executedOrders) {
                            currentOrder.getOrder().execute();
                            executedOrders.add(currentOrder.getOrder());
                            System.out.println(this.getName() + " executed the " + currentOrder.getOrder().getName());
                            currentOrder.setOrder(null);
                            dispatcher.notifyAvailable(this);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException("Error during order execution", e);
                    }
                }
            }
        }
    }

    @Override
    public void placeOrder(Order order) {
        synchronized (currentOrder) {
            currentOrder.setOrder(order);
            currentOrder.notify();
        }
    }

    @Override
    public List<Order> getExecutedOrders() {
        synchronized (executedOrders) {
            return new ArrayList<>(executedOrders);
        }
    }
}

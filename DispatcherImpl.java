package main.java.gookjiii.taxi;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
public class DispatcherImpl implements Dispatcher {

    private final Queue<Taxi> availableTaxis;
    private final List<Order> orders;
    private final AtomicBoolean execute;
    private final int taxiCount;
    private final int orderCount;

    public DispatcherImpl(int taxiCount, int orderCount) {
        this.taxiCount = taxiCount;
        this.orderCount = orderCount;
        this.availableTaxis = new ArrayDeque<>(taxiCount);
        this.orders = new ArrayList<>(orderCount);
        this.execute = new AtomicBoolean(true);
        initializeOrders(this.orderCount);
        initializeTaxis(this.taxiCount);
    }

    private void initializeTaxis(int taxiCount) {
        for (int i = 0; i < taxiCount; ++i) {
            TaxiImpl taxi = new TaxiImpl("Taxi " + i, this, execute);
            taxi.start();
            availableTaxis.add(taxi);
        }
    }

    private void initializeOrders(int orderCount) {
        for (int i = 0; i < orderCount; ++i) {
            orders.add(new Order("Order " + i, OrderType.PENDING));
        }
    }

    @Override
    public void run() {
        synchronized (availableTaxis) {
            for (Order order : orders) {
                while (availableTaxis.isEmpty()) {
                    try {
                        availableTaxis.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException("Error during execution", e);
                    }
                }
                Taxi freeTaxi = availableTaxis.poll();
                placeOrder(freeTaxi, order);
            }
        }
        endDay();
    }

    private void endDay() {
        execute.set(false);
        synchronized (availableTaxis) {
            while (availableTaxis.size() != this.taxiCount) {
                try {
                    availableTaxis.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException("Error during ending the day", e);
                }
            }
            Queue<Taxi> toEndList = new ArrayDeque<>(availableTaxis);
            while (!toEndList.isEmpty()) {
                Taxi taxi = toEndList.poll();
                placeOrder(taxi, new Order("day ended", OrderType.COMPLETE));
                try {
                    ((TaxiImpl) taxi).join();
                } catch (InterruptedException e) {
                    throw new RuntimeException("Error during taxi join", e);
                }
                System.out.println(((TaxiImpl) taxi).getName() + " : " + taxi.getExecutedOrders());
            }
        }
    }

    @Override
    public void placeOrder(Taxi taxi, Order order) {
        taxi.placeOrder(order);
    }

    @Override
    public void notifyAvailable(Taxi taxi) {
        synchronized (availableTaxis) {
            availableTaxis.add(taxi);
            availableTaxis.notify();
        }
    }
}

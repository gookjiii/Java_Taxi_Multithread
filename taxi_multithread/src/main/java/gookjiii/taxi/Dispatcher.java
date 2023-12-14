package main.java.gookjiii.taxi;

public interface Dispatcher {

    void run();
    void placeOrder(Taxi taxi, Order order);

    void notifyAvailable(Taxi taxi);
}

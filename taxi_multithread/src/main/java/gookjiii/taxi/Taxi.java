package main.java.gookjiii.taxi;

import java.util.List;

public interface Taxi{
    void placeOrder(Order order);

    List<Order> getExecutedOrders();
}

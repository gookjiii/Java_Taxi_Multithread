package main.java.gookjiii.taxi;
public class OrderWrapper {
    private Order order;

    public OrderWrapper() {
        order = null;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public boolean isNull() {
        return order == null;
    }
}
package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private double discount; // absolute discount amount
    private double netAmount;
    private LocalDateTime orderTime;
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
        orderTime = LocalDateTime.now();
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
    public double getNetAmount() { return netAmount; }
    public void setNetAmount(double netAmount) { this.netAmount = netAmount; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public List<OrderItem> getItems() { return items; }

    public double getSubtotal() {
        return items.stream().mapToDouble(OrderItem::getTotal).sum();
    }
}
